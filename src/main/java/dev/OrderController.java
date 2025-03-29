package dev;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable int orderId) {
        return orderService.getOrder(orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(order));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable int orderId, @RequestBody Order order) {
        return orderService.updateOrder(orderId, order)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PatchMapping("/{orderId}/products")
    public ResponseEntity<Order> addProduct(@PathVariable int orderId, @RequestBody Product product) {
        return orderService.addProduct(orderId, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity<Order> removeProduct(@PathVariable int orderId, @PathVariable int productId) {
        return orderService.removeProduct(orderId, productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int orderId) {
        return orderService.deleteOrder(orderId) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}

@Service
class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Optional<Order> getOrder(int orderId) {
        return orderRepository.findById(orderId);
    }

    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> updateOrder(int orderId, Order newOrder) {
        if (!orderRepository.existsById(orderId)) return Optional.empty();
        newOrder.setId(orderId);
        return Optional.of(orderRepository.save(newOrder));
    }

    public Optional<Order> addProduct(int orderId, Product product) {
        return orderRepository.findById(orderId).map(order -> {
            order.getProducts().add(product);
            return orderRepository.save(order);
        });
    }

    public Optional<Order> removeProduct(int orderId, int productId) {
        return orderRepository.findById(orderId).map(order -> {
            order.getProducts().removeIf(p -> p.getId() == productId);
            return orderRepository.save(order);
        });
    }

    public boolean deleteOrder(int orderId) {
        if (!orderRepository.existsById(orderId)) return false;
        orderRepository.deleteById(orderId);
        return true;
    }
}

@Repository
interface OrderRepository extends JpaRepository<Order, Integer> {}

@Entity
class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private double price;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}

@Entity
class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}