import { Component } from '@angular/core';
import { CarouselComponent } from '../templates/carousel/carousel.component';
import { AlbumComponent } from '../templates/album/album.component';
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [CarouselComponent, AlbumComponent, NgOptimizedImage],
  styleUrl: './home.component.css',
  templateUrl: './home.component.html'
  
})

export class HomeComponent {}
