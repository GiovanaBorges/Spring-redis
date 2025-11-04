global:
  scrape_interval: 5s # a cada 5 segundos prometheus coleta as métricas

scrape_configs:
  - job_name: 'spring-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['${SPRING_HOST_IP}:8080']  # usa variável de ambiente
