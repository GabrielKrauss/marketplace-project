package com.project.marketplace.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import com.project.marketplace.entities.Coupon;
import com.project.marketplace.repositories.CouponRepository;
import com.project.marketplace.services.exceptions.DatabaseException;
import com.project.marketplace.services.exceptions.InvalidDataException;
import com.project.marketplace.services.exceptions.ObjectAlreadyExistsException;
import com.project.marketplace.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

public class CouponServiceTest {

    @Mock
    private CouponRepository repository;

    @InjectMocks
    private CouponService service;

    private Coupon coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        coupon = new Coupon(1L, "COUPON123", 10.0, 0.0, true);
    }
    
    @Test
    void testFindAllSuccess() {
    	Coupon coup1 = new Coupon(1L, "COUPON123", 10.0, 0.0, true);
    	Coupon coup2 = new Coupon(2L, "COUPON1243", 15.0, 0.0, true);
        List<Coupon> coupons = List.of(coup1, coup2);
        when(repository.findAll()).thenReturn(coupons);

        List<Coupon> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("COUPON123", result.get(0).getCode());
        assertEquals("COUPON1243", result.get(1).getCode());
    }
    
    @Test
    void testFindById_whenNotFound_shouldThrowResourceNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void testFindById_whenFound_shouldReturnCoupon() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(coupon));
        
        Coupon result = service.findById(1L);
        
        assertEquals(coupon, result);
    }

    @Test
    void testInsert_whenCouponAlreadyExists_shouldThrowObjectAlreadyExistsException() {
        when(repository.existsByCode(anyString())).thenReturn(true);
        
        assertThrows(ObjectAlreadyExistsException.class, () -> service.insert(coupon));
    }

    @Test
    void testInsert_whenInvalidDiscount_shouldThrowInvalidDataException() {
        coupon.setDiscountValue(10.0);
        coupon.setDiscountPercentage(5.0);

        assertThrows(InvalidDataException.class, () -> service.insert(coupon));
    }

    @Test
    void testInsert_whenValidCoupon_shouldReturnSavedCoupon() {
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(Coupon.class))).thenReturn(coupon);
        
        Coupon result = service.insert(coupon);
        
        assertEquals(coupon, result);
    }

    @Test
    void testDelete_whenCouponNotFound_shouldThrowResourceNotFoundException() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }

    @Test
    void testDelete_whenCouponFound_shouldSetIsActiveFalse() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(coupon));
        when(repository.save(any(Coupon.class))).thenReturn(coupon);
        
        service.delete(1L);
        
        assertFalse(coupon.getIsActive());
    }

    @Test
    void testUpdate_whenCouponNotFound_shouldThrowResourceNotFoundException() {
        when(repository.getReferenceById(anyLong())).thenThrow(EntityNotFoundException.class);
        
        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, coupon));
    }

    @Test
    void testUpdate_whenCouponAlreadyExists_shouldThrowObjectAlreadyExistsException() {
        Coupon existingCoupon = new Coupon(2L, "COUPON123", 5.0, 0.0, true); // ID diferente

        when(repository.existsByCode(anyString())).thenReturn(true);
        when(repository.findByCode(anyString())).thenReturn(Optional.of(existingCoupon)); // Retorna um cupom diferente

        assertThrows(ObjectAlreadyExistsException.class, () -> service.update(1L, coupon));
    }

    @Test
    void testUpdate_whenValidCoupon_shouldReturnUpdatedCoupon() {
        when(repository.getReferenceById(anyLong())).thenReturn(coupon);
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(Coupon.class))).thenReturn(coupon);
        
        Coupon result = service.update(1L, coupon);
        
        assertEquals(coupon, result);
    }
    @Test
    void testUpdate_whenPercentageAndValueNotZero_shouldThrowInvalidDataException() {
        Coupon updatedCoupon = new Coupon(1L, "NEWCODE", 15.0, 5.0, false);
        assertThrows(InvalidDataException.class, () -> service.update(1L, updatedCoupon));
    }
    
    @Test
    void testUpdateData_whenCouponIsUpdated_shouldUpdateFieldsCorrectly() {
        Coupon updatedCoupon = new Coupon(1L, "NEWCODE", 5.0, 15.0, false);
        
        service.updateData(coupon, updatedCoupon);
 
        assertEquals("NEWCODE", coupon.getCode());
        assertFalse(coupon.getIsActive());
        assertEquals(15.0, coupon.getDiscountPercentage());
        assertEquals(5.0, coupon.getDiscountValue());
        
    }

    @Test
    void testUpdateData_whenCouponFieldsAreNull_shouldKeepOldValues() {
        Coupon updatedCoupon = new Coupon(null, null, 0.0, 0.0, null);
        
        service.updateData(coupon, updatedCoupon);
        
        assertEquals("COUPON123", coupon.getCode()); 
        assertTrue(coupon.getIsActive());
        assertEquals(0.0, coupon.getDiscountPercentage()); 
        assertEquals(10.0, coupon.getDiscountValue()); 
        
    }
    
    @Test
    void delete_shouldThrowDatabaseException_whenDataIntegrityViolationOccurs() {
        // Arrange
        Long couponId = 1L;
        Coupon existingCoupon = new Coupon(couponId, "DISCOUNT10", 10.0, 0.0, true); // Simula um cupom existente
        
        // Simula o comportamento do repositório
        when(repository.findById(couponId)).thenReturn(Optional.of(existingCoupon));
        when(repository.save(existingCoupon)).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(DatabaseException.class, () -> service.delete(couponId));

        // Verifica as chamadas
        verify(repository, times(1)).findById(couponId);
        verify(repository, times(1)).save(existingCoupon);
    }
    
    @Test
    void testFindAllActive_shouldReturnOnlyActiveCoupons() {
        Coupon activeCoupon1 = new Coupon(1L, "ACTIVE1", 10.0, 0.0, true);
        Coupon activeCoupon2 = new Coupon(2L, "ACTIVE2", 5.0, 0.0, true);
        List<Coupon> activeCoupons = List.of(activeCoupon1, activeCoupon2);

        when(repository.findAllActiveCoupons()).thenReturn(activeCoupons);

        List<Coupon> result = service.findAllActive();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(Coupon::getIsActive));
    }

    @Test
    void testFindByCode_whenFound_shouldReturnCoupon() {
        when(repository.findByCode(anyString())).thenReturn(Optional.of(coupon));

        Coupon result = service.findByCode("COUPON123");

        assertEquals(coupon, result);
    }

    @Test
    void testFindByCode_whenNotFound_shouldThrowResourceNotFoundException() {
        when(repository.findByCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByCode("INVALID_CODE"));
    }
}
