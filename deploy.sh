#!/bin/bash

APP_VER=${VERSION:-latest}
REP_CNT=${REPLICAS:-1}

cat << EOF > deploy.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ledweb-deploy
  labels:
    app: ledweb
spec:
  replicas: $REP_CNT
  selector:
    matchLabels:
      app: ledweb
  template:
    metadata:
      labels:
        app: ledweb
    spec:
      volumes:
        - name: my-volume
          hostPath:
            path: /var/fifo
      containers:
      - name: ledweb 
        image: 192.168.0.200:5000/k8sled/rpi-led:$APP_VER
        ports:
        - containerPort: 8080
        volumeMounts:
         - name: my-volume
           mountPath: /var/fifo
      imagePullSecrets:
        - name: registrypullsecret
EOF

kubectl apply -f deploy.yaml
