# Workshop Springboot Tutorial

## Crear proyecto Springboot

Para crear el esqueleto del proyecto Springboot lo mas facil es utilizar la herramienta online de
`Spring Initializr`.

Para ello debemos acceder a la siguiente URL: https://start.spring.io/

Para este workshop llenaremos el formulario con la siguiente información:

![](./images/start_spring_io_001.png)

### Dependencias

Vamos a seleccionar la siguientes dependencias:
* `Spring WEB` la cual proveera todas las clases para crear servicios web
* `Spring Data JPA` (Java Persistance API) para trabajar con los accesos a base de datos
* `H2 Database` lo que nos permitirá crear una base de datos en memoria
* `Lombok` Ayuda con algunas de las molestias de Java, cosas como generar getters y setters con anotaciones y mucho mas

![](./images/start_spring_io_002.png)

Para finalizar precionaremos el botoón Generate el cual nos descargará un ZIP con el proyecto el cual
vamos a descomprimir en una carpeta a elección 

![](./images/start_spring_io_003.png)

## Importar proyecto

Para este workshop o cualquier proyecto Java que te toque trabajar recomendamos usar IntelliJ IDEA, el cual cuenta
con una versión Community que es gratuita y lo puedes descargar en la siguiente URL: https://www.jetbrains.com/idea/
 
Si bien puedes abrir el proyecto en cualquier IDE o editor de texto, asumiremos para el resto del workshop que
estas utilizando el que te hemos recomendado.
 
### Abrir IntelliJ IDEA

Al ejecutar el IDE te encontraras con una ventana similar a esta:

![](./images/open_project.png)

Seleccionar la carpeta que haz descompimido con el esqueleto del proyecto

![](./images/select_project.png)

Una vez que se ha terminado de importar el proyecto y descargar las dependencias, deberiamos tener una estructura
como sigue:

![](./images/project.png)

## Manos a la Obra!

Comenzaremos a crear nuestro poyecto de abajo hacia arriba (desde la capa de datos al servicio)

### Capa de datos

#### Configuración

Dado que hemos incluido la dependencia de H2 en nuestro proyecto, Spring detectara automaticamente que es
esta la base de datos que vamos a usar.

Para disponer de una consola web donde podamos manupularla y ademas activar que nos muestre por consola el
codigo SQL que va generando debemos agregar las siguientes lineas en el archivo 
`src/main/resources/application.properties`

```properties
# Enabling H2 Console
spring.h2.console.enabled=true
spring.jpa.properties.hibernate.show_sql=true
```

#### Entity

Ahora vamos a crear un `Entity` que represente un `Producto` en nuestra base de datos. Una buena practica en
Java es que hagamos todo en ingles, por lo que lo vamos a llamar `ProductEntity` y lo vamos a colocar en un 
paquete apropiado que llamaremos `entity` (Otra buena practica en Java es que usemos siempre singular, tanto
para nombrar clases como tambien paquetes).

Primero creamos el package el cual dejaremos dentro de `cl.continuum.product`

![](./images/create_package.png)

![](./images/create_package2.png)

Luego creamos la clase Entity:

![](./images/create_class.png)

![](./images/create_class2.png)

Agregaremos a nuestra clase las siguientes anotaciones:
* `@Entity` con lo cual Spring podrá identificar la clase como un `Entity`
* `@ToString` de `Lombok` nos va a generar una sobre carga del metodo toString de `java.lang.Object` en el cual
incluira todas las propiedades del objecto en la salida
* `@Getter` & `@Setter` de `Lombok` nos van a generar todo el código con los setter y getters de cada
propiedad del Bean
* `@AllArgsConstructor` de `Lombok` nos proveera de un cosntructor de clase que solicitara cada una de
las propiedades del Bean
* `@NoArgsConstructor` de `Lombok` nos proveera de un constructor por defecto (sin ningun parámetro)

Agregaremos en nuestro entity las propiedades que queremos que contenga la tabla `product` en base de datos
y crearemos una propiedad llamada `id` que será númerica la cual indicaremos mediante la anotación de JPA `@Id`
que será la llave de nuestra tabla. Adicionalmente usando la anotación de JPA `@GeneratedValue` le indicaremos
que esta llave se ira auto incrementando, por lo que nosotros no debemos preocuparnos de llenarla.

Nuestra clase debería verse asi cuando este lista:

```java
package cl.continuum.product.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long price;
    private LocalDateTime created;
    private LocalDateTime updated;
}
```

#### Repository

Los repository son interfaces que nos provee Spring para simplificar el uso de los Entity y las operaciones
que podemos relizar sobre ellos.

Para este workshop vamos a utilizar especificamente la interfaz `CrudRepository`, la que nos provee listas todas las
operaciones de `CRUD` sobre una tabla o Entity.

Creamos el package `repository` dentro de `cl.continuum.product` y luego cramos la clase `ProductCrudRepository` dentro
del package que acabamos de crear. Esta vez debemos seleccionar la opción `Interface` en el menú.

![](./images/create_interface.png)

Luego debemos hacer que esta interface extienda la interface de Spring `CrudRepository` la que nos proveera de las
operaciones basicas de `CRUD` sobre la tabla `product` a travez de nuestro entity `ProductEntity` con tan solo
indicarselo en sus tipos genericos:

```java
public interface ProductCrudRepository extends CrudRepository<ProductEntity, Long>
```

Dado que su llave primaria la hemos definido de tipo `Long` es que le hemos indicado en su segundo tipo generico este
tipo de dato.

Una de las operaciones que debemos realizar para el funcionamiento de nuestro servicio será la de buscar productos por
nombre, para lo cual querremos hacer un `LIKE` sobre el campo `name` en la tabla producto. Esta operación si bien no
viene ya lista en la interfaz de Spring, dado que no tiene como conocer cada nombre que podriamos tener en nuestras tablas,
crearla es muy sencillo, basta con que declaremos el siguiente metodo en nuestra interfaz:

```java
List<ProductEntity> findByNameLike(String name);
```

Con esto Spring sabe que lo que debe hacer cuando invoquemos este campo es crear una consulta como esta:

```sql
select * from product p where p.name like '${name}'
```

donde `${name}` será lo que le entreguemos como parametro al metodo.

Ya terminada nuestra interfaz debería lucir asi:

```java
package cl.continuum.product.repository;

import cl.continuum.product.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductCrudRepository extends CrudRepository<ProductEntity, Long> {
    List<ProductEntity> findByNameLike(String name);
}
```

#### Modelo

El modelo son los objetos que usualmente usaremos para transaportar informacion desde y hacia nuestro servicio. Es
bastante comnún que podamos tener una clase modelo que represente la información que tenemos en un entity, dado que el
modelado de datos de nuestra base de datos muchas veces coincide con el modelo de información que nuestro servicio hará
transitar. Esta no será la excepción y vamos a crear una clase de prodelo que llamaremos `Product`.

Antes de crear nuestra clase vamos a crear el package `model` dentro del package `cl.continuum.product` y luego creamos
nuestra clase dentro del package que hemos creado recien.

Nuestra clase deberá verse así:

```java
package cl.continuum.product.model;

import lombok.*;

import javax.validation.constraints.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Product {
    private Long id;

    @NotEmpty(message = "Please provide a name")
    @Size(min=3, max=200)
    private String name;

    @NotNull(message = "Please provide a price")
    @Min(1)
    @Max(1000000)
    private Long price;

    private List<Rating> detail;
}
```

Dado que este es un Bean usaremos nuevamente las anotaciones de `Lombok` que ya hemos descrito previamente.

Lo nuevo que vemos en esta clase son las anotaciones `@NotEmpty`, `@Size`, `@NotNull`, `@Min` y `@Max` las cuales son
constrains de Java que nos facilitaran las validación automatica de dichas propiedades.

Adicionalmente vamos a necesitar un modelo llamado `Rating` el cual nos servira para interactuar con el servicio externo
que provee información extra de cada producto. Este modelo deberá verse como sigue:

```java
package cl.continuum.product.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Rating {
    private Long id;
    private String productName;
    private Integer rating;
    private Integer commentsCount;
    private Long price;
    private String ecommerce;
}
```
#### Resource    

Un recurso es un objeto con, datos asociados, relaciones con otros recursos y un conjunto de métodos que operan sobre él

Spring provee la anotacion @RestController, para facilitar la implementacion de los metodos definidos por la arquitectura Rest

Creamos el package `resource` dentro de `cl.continuum.product` y luego cramos la clase `ProductResource` dentro
del package que acabamos de crear. Esta vez debemos seleccionar la opción `class` en el menú.

![](./images/resource_class.png)

Luego debemos agregar la anotacion `@RestController`  sobre la definicion de `class`, esta anotacion es utilizada para marcar un objeto como `request handler` y generalmente se utiliza para crear servicios RESTful. Tambien debemos agregar la anotacion @RequestMapping(path = "/products", produces = "application/json")` , esta anotacion se utiliza para mapear `requests` con clases o metodos :

`@RestController
 @RequestMapping(path = "/products", produces = "application/json")
 public class ProductResource`

Como ya mencionamos antes la clase `Resource` contiene metodos que nos permitiran ejecutar operaciones sobre este y otros recursos, la firma de estas operaciones esta contenida en interfaces llamadas  `Service` y la implementacion en clases `ServiceImpl`. Para poder acceder a las implementaciones de nuestros servicios `Spring` nos provee la inyeccion de dependencia que tambien facilitara la escabilidad y manejo del ciclo de vida de estas implementaciones.
`    @Autowired
     private ProductService productService;
     @Autowired
     private DetailService detailService;`

Para finalizar definiremos los siguientes `end points`, utilizaremos la anotacion @GetMapping la cual se utiliza para mapear request GET y la anotacion @PostMapping que se utiliza para mapear operaciones POST

```java
 @GetMapping(path = "")
     ResponseEntity<?> getProducts(@RequestParam(required = false, name = "name") String name) {
         List<Product> products = productService.list(name);
         if (products == null || products.isEmpty()) {
             return ResponseEntity.status(404).build();
         }
         return ResponseEntity.ok(products);
     }
     @PostMapping(path = "", consumes = "application/json")
     ResponseEntity<?> createProduct(@Valid @RequestBody(required = true) Product product) {
         product = productService.add(product);
         return ResponseEntity.status(201).body(product);
     }
     @GetMapping(path = "/{id}")
     ResponseEntity<?> getProduct(@PathVariable(required = false, name = "id") Long id) {
         Product product = productService.get(id);
         if (product == null) {
             return ResponseEntity.status(404).build();
         }
         List detail = detailService.get(product.getName());
         product.setDetail(detail);
         return ResponseEntity.ok(product);
     }
```
Ya terminada nuestra clase debería lucir asi:

```java
package cl.continuum.product.resource;

import cl.continuum.product.model.Product;
import cl.continuum.product.service.DetailService;
import cl.continuum.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/products", produces = "application/json")
public class ProductResource {

    @Autowired
    private ProductService productService;

    @Autowired
    private DetailService detailService;

    @GetMapping(path = "")
    ResponseEntity<?> getProducts(@RequestParam(required = false, name = "name") String name) {
        List<Product> products = productService.list(name);
        if (products == null || products.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping(path = "", consumes = "application/json")
    ResponseEntity<?> createProduct(@Valid @RequestBody(required = true) Product product) {
        product = productService.add(product);
        return ResponseEntity.status(201).body(product);
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<?> getProduct(@PathVariable(required = false, name = "id") Long id) {
        Product product = productService.get(id);
        if (product == null) {
            return ResponseEntity.status(404).build();
        }
        List detail = detailService.get(product.getName());
        product.setDetail(detail);
        return ResponseEntity.ok(product);
    }
}
```

#### Service    

El componente denominado service consta de interfaces y clases que implementaran dichas interfaces, este patron de diseño es utilizado fuertemente en java para desacoplar codigo y construir componentes altamente escalables.
para este workshop son utilizados para ejecutar las operaciones sobre nuestros recursos (Resources)

Creamos el package `service` dentro de `cl.continuum.product` y luego creamos las interfaces `DetailService` y `ProductService`  dentro
del package que acabamos de crear. Esta vez debemos seleccionar la opción `interface` en el menú.

![](./images/create_interface.png)

Luego vamos a crear las clases que implementaran duchas interfaces `DetailServiceImpl` y  `ProductServiceImpl` dentro del package que acabamos de crear. Esta vez debemos seleccionar la opción `class` en el menú.

![](./images/create_class2.png)

El contenio de las interfaces debe ser el siguiente:

```java
package cl.continuum.product.service;

import java.util.List;

public interface DetailService {

    List get(String name);
}
```

```java
import cl.continuum.product.model.Product;

import java.util.List;

public interface ProductService {
    Product add(Product product);
    List<Product> list(String name);
    Product get(Long id);
}
```
La clase que implementa `DetailService` debe contener la anotacion `@Service` que se utiliza para marcar una clase que ejecuta operaciones. Tambien la anotacion `@Profile` que se utiliza para agrupar logicamente y que ppuede ser activado programadamente.


```java
 @Service
 @Profile("prod")
 public class DetailServiceImpl implements DetailService{}
```

Haremos lo mismo con `ProductServiceImpl`

```java
 @Service
 @Profile("prod")
 public class ProductServiceImpl implements ProductService {}
```

Utilizaremos  inyeccion de dependencia para obtener un objeto `RestTemplate` en nuestra clase implementacion `DetailServiceImpl` el cual nos permitira consumir un microservicio

```java
 private static final String SERVICE_HOST = "http://localhost:8082";

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }
```

Utilizaremos  inyeccion de dependencia para obtener nuestro objeto `Repository` en nuestra clase implementacion `ProductServiceImpl`

```java
@Autowired
private ProductCrudRepository productRepository;
```


Cada clase que implementa una interfaz esta obligada a implementar las firmas de los metodos definidos en estas, a continuacion los metodos para cada clase

`DetailServiceImpl`
```java
@Override
    @SuppressWarnings(value = "unchecked")
    public List get(String name) {
        try {
            String url = String.format("%s/api/v1/rating", SERVICE_HOST);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("name", name);
            //ResponseEntity<List> resp = restTemplate().getForEntity(builder.toUriString(), List.class);

            ResponseEntity<List<Rating>> resp =  restTemplate().exchange(
                    String.format("%s?name=%s",url,name),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Rating>>(){});
            if (resp.getStatusCode().value() != 200) {
                throw new Exception("Gateway Error");
            }
            return resp.getBody();
        } catch(Exception ex) {
            System.err.println(ex.getMessage() + " : " + ex.getCause());
            return null;
        }
    }
 
```

`ProductServiceImpl`
```java
@Override
    public Product add(Product product) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(product, productEntity);
        productEntity.setCreated(LocalDateTime.now());

        ProductEntity savedEntity = productRepository.save(productEntity);
        BeanUtils.copyProperties(savedEntity, product);

        return product;
    }

    @Override
    public List<Product> list(String name) {
        Iterable<ProductEntity> entities = null == name ? productRepository.findAll() : productRepository.findByNameLike("%" + name + "%");
        List<Product> products = new ArrayList<>();
        entities.forEach(entity -> {
            Product product = new Product();
            BeanUtils.copyProperties(entity, product);
            products.add(product);
        });

        return products;
    }

    @Override
    public Product get(Long id) {
        Product product = new Product();
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        productEntity.ifPresent(entity -> {
            BeanUtils.copyProperties(entity, product);
        });

        if (null == product.getId()) return null;
        return product;
    }
```

Ya terminada nuestras clases debería lucir asi:

```java
package cl.continuum.product.service;

import cl.continuum.product.model.Rating;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@Profile("prod")
public class DetailServiceImpl implements DetailService {

    private static final String SERVICE_HOST = "http://localhost:8082";

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List get(String name) {
        try {
            String url = String.format("%s/api/v1/rating", SERVICE_HOST);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("name", name);
            //ResponseEntity<List> resp = restTemplate().getForEntity(builder.toUriString(), List.class);

            ResponseEntity<List<Rating>> resp =  restTemplate().exchange(
                    String.format("%s?name=%s",url,name),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Rating>>(){});
            if (resp.getStatusCode().value() != 200) {
                throw new Exception("Gateway Error");
            }
            return resp.getBody();
        } catch(Exception ex) {
            System.err.println(ex.getMessage() + " : " + ex.getCause());
            return null;
        }
    }
}

```

```java

 package cl.continuum.product.service;
 
 import cl.continuum.product.entity.ProductEntity;
 import cl.continuum.product.model.Product;
 import cl.continuum.product.repository.ProductCrudRepository;
 import org.springframework.beans.BeanUtils;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Profile;
 import org.springframework.stereotype.Service;
 
 import java.time.LocalDateTime;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Optional;
 
 @Service
 @Profile("prod")
 public class ProductServiceImpl implements ProductService {
 
     @Autowired
     private ProductCrudRepository productRepository;
 
     @Override
     public Product add(Product product) {
         ProductEntity productEntity = new ProductEntity();
         BeanUtils.copyProperties(product, productEntity);
         productEntity.setCreated(LocalDateTime.now());
 
         ProductEntity savedEntity = productRepository.save(productEntity);
         BeanUtils.copyProperties(savedEntity, product);
 
         return product;
     }
 
     @Override
     public List<Product> list(String name) {
         Iterable<ProductEntity> entities = null == name ? productRepository.findAll() : productRepository.findByNameLike("%" + name + "%");
         List<Product> products = new ArrayList<>();
         entities.forEach(entity -> {
             Product product = new Product();
             BeanUtils.copyProperties(entity, product);
             products.add(product);
         });
 
         return products;
     }
 
     @Override
     public Product get(Long id) {
         Product product = new Product();
         Optional<ProductEntity> productEntity = productRepository.findById(id);
         productEntity.ifPresent(entity -> {
             BeanUtils.copyProperties(entity, product);
         });
 
         if (null == product.getId()) return null;
         return product;
     }
 }


```