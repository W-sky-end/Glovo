import dev.Order;
import dev.OrderRepository;
import dev.OrderService;
import dev.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        Product product = new Product();
        product.setId(1);
        product.setName("Burger");
        product.setPrice(250);

        testOrder = new Order();
        testOrder.setId(1);
        testOrder.setProducts(new ArrayList<>(List.of(product)));
    }

    @Test
    void testGetOrderFound() {
        Mockito.when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        Optional<Order> result = orderService.getOrder(1);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(1, result.get().getId());
    }

    @Test
    void testAddOrder() {
        Mockito.when(orderRepository.save(testOrder)).thenReturn(testOrder);
        Order result = orderService.addOrder(testOrder);
        Assertions.assertEquals(testOrder.getId(), result.getId());
    }

    @Test
    void testUpdateOrderNotFound() {
        Mockito.when(orderRepository.existsById(1)).thenReturn(false);
        Optional<Order> result = orderService.updateOrder(1, testOrder);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testAddProductToOrder() {
        Product newProduct = new Product();
        newProduct.setId(2);
        newProduct.setName("Fries");
        newProduct.setPrice(100);

        Mockito.when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        Mockito.when(orderRepository.save(Mockito.any(Order.class))).thenReturn(testOrder);

        Optional<Order> result = orderService.addProduct(1, newProduct);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(2, result.get().getProducts().size());
    }

    @Test
    void testDeleteOrder() {
        Mockito.when(orderRepository.existsById(1)).thenReturn(true);
        boolean result = orderService.deleteOrder(1);
        Assertions.assertTrue(result);
        Mockito.verify(orderRepository).deleteById(1);
    }
}
