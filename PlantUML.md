`https://www.plantuml.com/`
`https://kroki.io/`

@startuml
skinparam packageStyle rectangle
skinparam classAttributeIconSize 0

package "domain.entity" {
  class User
  class Plan
  class Subscription
}

package "domain.repository" {
  interface UserRepository
  interface PlanRepository
  interface SubscriptionRepository
}

package "application.dtos" {
  class CheckoutActivationRequestDto
  class ActivationResultDto
}

package "application.service" {
  class UserService
  class PlanService
  class SubscriptionService
  class ServiceRegistry
}

package "infrastructure.repository" {
  class JdbcUserRepository
  class JdbcPlanRepository
  class JdbcSubscriptionRepository
}

package "infrastructure.config" {
  class DatabaseConfig
}

package "infrastructure.persistence" {
  class DatabaseInitializer
}

package "interfaces.api.controller" {
  class RestServer
  class HttpUtils
  class JsonUtil
}

package "interfaces.api.response" {
  class ApiResponse
}

package "interfaces.cli" {
  class CliApplication
}

class App

' --- Implementações de repositório ---
JdbcUserRepository ..|> UserRepository
JdbcPlanRepository ..|> PlanRepository
JdbcSubscriptionRepository ..|> SubscriptionRepository

' --- Serviços usam contratos de repositório ---
UserService --> UserRepository
PlanService --> PlanRepository
SubscriptionService --> SubscriptionRepository
SubscriptionService --> UserRepository
SubscriptionService --> PlanRepository

ServiceRegistry --> UserService
ServiceRegistry --> PlanService
ServiceRegistry --> SubscriptionService

' --- API/CLI consomem serviços ---
RestServer --> ServiceRegistry
CliApplication --> ServiceRegistry
App --> RestServer
App --> CliApplication

' --- Controllers/Utils/DTOs ---
RestServer --> HttpUtils
RestServer --> JsonUtil
RestServer --> CheckoutActivationRequestDto
RestServer --> ActivationResultDto
RestServer --> ApiResponse

' --- Infraestrutura ---
DatabaseInitializer --> DatabaseConfig
JdbcUserRepository --> DatabaseConfig
JdbcPlanRepository --> DatabaseConfig
JdbcSubscriptionRepository --> DatabaseConfig

' --- Relações de domínio (ajustar conforme código) ---
User "1" --> "0..*" Subscription
Plan "1" --> "0..*" Subscription
Subscription --> User
Subscription --> Plan

@enduml