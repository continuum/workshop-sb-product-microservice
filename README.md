# workshop-sb-product-microservice
Springboot Microservices Continuum Workshop

### obtener todos los productos
    curl http://localhost:8080/api/v1/products
    
### obtener los productos por criterio name
    curl http://localhost:8080/api/v1/products?name=switch
    curl http://localhost:8080/api/v1/products?name=lite
    curl http://localhost:8080/api/v1/products?name=xbox

### crear un producto
    curl -X POST http://localhost:8080/api/v1/products -H "Content-Type: application/json" -d '{"name": "ps4", "price": 0}'
    curl -X POST http://localhost:8080/api/v1/products -H "Content-Type: application/json" -d '{"name": "ps4", "price": 300000}'

### obtener un producto por id
    curl http://localhost:8080/api/v1/products/0
    curl http://localhost:8080/api/v1/products/1