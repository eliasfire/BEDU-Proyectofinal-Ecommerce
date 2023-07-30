package bedu.proyecto.ecommerce.mockitotests.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import bedu.proyecto.ecommerce.controller.HomeController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import bedu.proyecto.ecommerce.model.DetalleOrden;
import bedu.proyecto.ecommerce.model.Orden;
import bedu.proyecto.ecommerce.model.Producto;
import bedu.proyecto.ecommerce.model.Usuario;
import bedu.proyecto.ecommerce.service.IDetalleOrdenService;
import bedu.proyecto.ecommerce.service.IOrdenService;
import bedu.proyecto.ecommerce.service.IUsuarioService;
import bedu.proyecto.ecommerce.service.ProductoService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HomeControllerTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private IOrdenService ordenService;

    @Mock
    private IDetalleOrdenService detalleOrdenService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHome() {
        // Arrange
        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto(1, "Product 1", "Description 1", "Categoria 1", "image1.jpg", 10.0, 5, new Usuario()));
        productos.add(new Producto(2, "Product 2", "Description 2", "Categoria 1", "image2.jpg", 20.0, 3, new Usuario()));
        when(productoService.findAll()).thenReturn(productos);

        HttpSession session = mock(HttpSession.class); // Mock the HttpSession
        when(session.getAttribute("idusuario")).thenReturn(null); // Set the session attribute

        // Act
        String viewName = homeController.home(model, session);

        // Assert
        verify(model).addAttribute(eq("productos"), anyList());
        verify(model).addAttribute(eq("sesion"), any());
        verify(session, times(2)).getAttribute("idusuario"); // Verify the invocation twice
        assertEquals("usuario/home", viewName);
    }


    @Test
    public void testProductoHome() {
        // Arrange
        Integer productId = 1;
        Producto producto = new Producto(1, "Product 1", "Description 1", "Categoria 1", "image1.jpg", 10.0, 5, new Usuario());
        when(productoService.get(productId)).thenReturn(Optional.of(producto));

        // Act
        String viewName = homeController.productoHome(productId, model, session);

        // Assert
        verify(model).addAttribute(eq("producto"), any(Producto.class));
        assertEquals("usuario/productohome", viewName);
    }

    @Test
    void testAddCart() {
        // Arrange
        Integer productId = 1;
        Integer quantity = 2;
        Producto producto = new Producto(1, "Product 1", "Description 1", "Categoria 1", "image1.jpg", 10.0, 5, new Usuario());
        when(productoService.get(productId)).thenReturn(Optional.of(producto));

        // Act
        String viewName = homeController.addCart(productId, quantity, model,session);

        // Assert
        verify(model).addAttribute(eq("cart"), anyList());
        verify(model).addAttribute(eq("orden"), any(Orden.class));
        verify(model).addAttribute("cart", homeController.getDetalles());
        verify(model).addAttribute("orden", homeController.getOrden());
        assertEquals("usuario/carrito", viewName);
    }

    @Test
    void testDeleteProductoCart() {
        // Arrange
        Integer productId = 1;
        DetalleOrden detalleOrden1 = new DetalleOrden(1, "Product 1", 5, 10.0, 50.0);
        DetalleOrden detalleOrden2 = new DetalleOrden(2, "Product 2", 3, 20.0, 60.0);
        List<DetalleOrden> detalles = new ArrayList<>();
        detalles.add(detalleOrden1);
        detalles.add(detalleOrden2);
        homeController.setDetalles(detalles);

        // Act
        String viewName = homeController.deleteProductoCart(productId, model);

        // Assert
        verify(model).addAttribute(eq("cart"), anyList());
        verify(model).addAttribute(eq("orden"), any(Orden.class));
        verify(model).addAttribute("cart", homeController.getDetalles());
        verify(model).addAttribute("orden", homeController.getOrden());
        assertEquals("usuario/carrito", viewName);
        assertEquals(0, homeController.getDetalles().size());
        assertFalse(homeController.getDetalles().stream().anyMatch(dt -> dt.getProducto().getId() == productId));
    }

    @Test
    void testGetCart() {
        // Arrange
        List<DetalleOrden> detalles = new ArrayList<>();
        homeController.setDetalles(detalles);

        // Act
        String viewName = homeController.getCart(model, session);

        // Assert
        verify(model).addAttribute(eq("cart"), anyList());
        verify(model).addAttribute(eq("orden"), any(Orden.class));
        verify(model).addAttribute(eq("sesion"), any());
        verify(model).addAttribute("cart", homeController.getDetalles());
        verify(model).addAttribute("orden", homeController.getOrden());
        verify(session).getAttribute("idusuario");
        assertEquals("/usuario/carrito", viewName);
    }

    @Test
    void testOrder() {
        // Arrange
        Integer userId = 1;
        Integer orderId = 1;
        Usuario usuario = new Usuario();
        usuario.setId(userId);
        DetalleOrden detalleOrden1 = new DetalleOrden(1, "Product 1", 5, 10.0, 50.0);
        DetalleOrden detalleOrden2 = new DetalleOrden(2, "Product 2", 3, 20.0, 60.0);
        List<DetalleOrden> detalles = new ArrayList<>();
        detalles.add(detalleOrden1);
        detalles.add(detalleOrden2);
        homeController.setDetalles(detalles);
        when(usuarioService.findById(userId)).thenReturn(Optional.of(usuario));

        HttpSession session = mock(HttpSession.class); // Mock the HttpSession
        when(session.getAttribute("idusuario")).thenReturn(userId.toString()); // Set the session attribute

        // Act
        String viewName = homeController.order(model, session);

        // Assert
        verify(model).addAttribute(eq("cart"), anyList());
        verify(model).addAttribute(eq("orden"), any(Orden.class));
        verify(model).addAttribute(eq("usuario"), any(Usuario.class));
        verify(model).addAttribute("cart", homeController.getDetalles());
        verify(model).addAttribute("orden", homeController.getOrden());
        verify(model).addAttribute("usuario", usuario);
        assertEquals("usuario/resumenorden", viewName);
    }

    @Test
    void testSaveOrder() {
        // Preparar datos de prueba
        Usuario usuario = new Usuario();
        usuario.setId(1);
        when(session.getAttribute("idusuario")).thenReturn("1");
        when(usuarioService.findById(1)).thenReturn(Optional.of(usuario));
        when(ordenService.generarNumeroOrden()).thenReturn("12345");

        // Llamar al método a probar
        String result = homeController.saveOrder(session);

        // Verificar resultados
        assertEquals("redirect:/", result);
        verify(ordenService).save(any(Orden.class));
        verify(detalleOrdenService, times(homeController.getDetalles().size())).save(any(DetalleOrden.class));
    }

    @Test
    void testSearchProduct() {
        // Arrange
        String productName = "Product";
        List<Producto> productos = new ArrayList<>();
        productos.add(new Producto(1, "Product 1", "Description 1", "Categoria 1", "image1.jpg", 10.0, 5, new Usuario()));
        productos.add(new Producto(2, "Product 2", "Description 2", "Categoria 1", "image2.jpg", 20.0, 3, new Usuario()));
        when(productoService.findAll()).thenReturn(productos);

        // Act
        String viewName = homeController.searchProduct(productName, model);

        // Assert
        verify(model).addAttribute(eq("productos"), anyList());
        verify(productoService).findAll();
        assertEquals("usuario/home", viewName);
    }
}

