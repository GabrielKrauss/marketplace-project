#!/bin/bash

# Getting pod names
POD_FRONTEND=$(kubectl get pods | grep 'angular-app' | awk '{print $1}')
POD_BACKEND=$(kubectl get pods | grep 'marketplace-deployment' | awk '{print $1}')
POD_KEYCLOAK=$(kubectl get pods | grep 'keycloak-deployment' | awk '{print $1}')
POD_DATABASE=$(kubectl get pods | grep 'postgresql' | awk '{print $1}')

# Verify if all pods was found
if [[ -z "$POD_FRONTEND" || -z "$POD_BACKEND" || -z "$POD_KEYCLOAK" || -z "$POD_DATABASE" ]]; then
  echo "Erro: Não foi possível encontrar todos os pods necessários!"
  exit 1
fi

# Port-forwarding frontend
echo "Port-forwarding frontend ($POD_FRONTEND)..."
kubectl port-forward pod/$POD_FRONTEND 4200:4200 &

# Port-forwarding backend
echo "Port-forwarding backend ($POD_BACKEND)..."
kubectl port-forward pod/$POD_BACKEND 8080:8080 &

# Port-forwarding keycloak
echo "Port-forwarding keycloak ($POD_KEYCLOAK)..."
kubectl port-forward pod/$POD_KEYCLOAK 8081:8081 &

# Port-forwarding keycloak
echo "Port-forwarding keycloak ($POD_DATABASE)..."
kubectl port-forward pod/$POD_DATABASE 5432:5432 &

# Waiting all port-forward process
wait