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

crear package model

![](./images/create_package.png)
![](./images/model_package.png)

crear class entity

![](./images/create_class.png)
![](./images/create_class2.png)
![](./images/entity_content.png)

