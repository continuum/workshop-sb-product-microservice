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

`public interface ProductCrudRepository extends CrudRepository<ProductEntity, Long>`

Dado que su llave primaria la hemos definido de tipo `Long` es que le hemos indicado en su segundo tipo generico este
tipo de dato.

Una de las operaciones que debemos realizar para el funcionamiento de nuestro servicio será la de buscar productos por
nombre, para lo cual querremos hacer un `LIKE` sobre el campo `name` en la tabla producto. Esta operación si bien no
viene ya lista en la interfaz de Spring, dado que no tiene como conocer cada nombre que podriamos tener en nuestras tablas,
crearla es muy sencillo, basta con que declaremos el siguiente metodo en nuestra interfaz:

`List<ProductEntity> findByNameLike(String name);`

Con esto Spring sabe que lo que debe hacer cuando invoquemos este campo es crear una consulta como esta:

```sql
select * from product p where p.name like '$NAME'
```

donde `$NAME` sera lo que le entreguemos como parametro al metodo.

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
