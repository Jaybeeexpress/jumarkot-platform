output "eks_cluster_endpoint" {
  description = "EKS cluster API endpoint"
  value       = module.eks.cluster_endpoint
  sensitive   = true
}

output "rds_endpoint" {
  description = "RDS PostgreSQL endpoint"
  value       = module.rds.db_instance_endpoint
}

output "redis_endpoint" {
  description = "ElastiCache Redis endpoint"
  value       = module.redis.cache_nodes[0].address
}

output "kafka_bootstrap_brokers" {
  description = "MSK Kafka bootstrap broker string"
  value       = module.msk.bootstrap_brokers_tls
  sensitive   = true
}
