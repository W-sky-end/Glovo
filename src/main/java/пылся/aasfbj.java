import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final Map<Integer, Order> orders = new HashMap<>();

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable int orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<Order> addOrder(@RequestBody Order order) {
        if (orders.containsKey(order.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        orders.put(order.getId(), order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<Order> updateOrder(@PathVariable int orderId, @RequestBody Order order) {
        if (!orders.containsKey(orderId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        orders.put(orderId, order);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/products")
    public ResponseEntity<Order> addProduct(@PathVariable int orderId, @RequestBody Product product) {
        Order order = orders.get(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        order.getProducts().add(product);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{orderId}/products/{productId}")
    public ResponseEntity<Order> removeProduct(@PathVariable int orderId, @PathVariable int productId) {
        Order order = orders.get(orderId);
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        order.getProducts().removeIf(p -> p.getId() == productId);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int orderId) {
        if (!orders.containsKey(orderId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        orders.remove(orderId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

class Product {
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

class Order {
    private int id;
    private List<Product> products = new ArrayList<>();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}