package bedu.proyecto.ecommerce.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import bedu.proyecto.ecommerce.model.Orden;
import bedu.proyecto.ecommerce.model.Producto;
import bedu.proyecto.ecommerce.service.IOrdenService;
import bedu.proyecto.ecommerce.service.IUsuarioService;
import bedu.proyecto.ecommerce.service.ProductoService;

@Slf4j
@Controller
@RequestMapping("/administrador")
public class AdministradorController {

	@Autowired
	private ProductoService productoService;
	
	@Autowired
	private IUsuarioService usuarioService;
	
	@Autowired
	private IOrdenService ordensService;

	@GetMapping("")
	public String home(Model model) {

		List<Producto> productos = productoService.findAll();
		model.addAttribute("productos", productos);


		return "administrador/home";
	}
	
	@GetMapping("/usuarios")
	public String usuarios(Model model) {
		model.addAttribute("usuarios", usuarioService.findAll());
		return "administrador/usuarios";
	}
	
	@GetMapping("/ordenes")
	public String ordenes(Model model) {
		model.addAttribute("ordenes", ordensService.findAll());
		return "administrador/ordenes";
	}
	
	@GetMapping("/detalle/{id}")
	public String detalle(Model model, @PathVariable Integer id) {
		log.info("Id de la orden {}",id);
		Orden orden= ordensService.findById(id).get();
		
		model.addAttribute("detalles", orden.getDetalle());
		
		return "administrador/detalleorden";
	}

}
