package dominio.integracion;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String CODIGO_3_VOCALES = "FE1TSA015U";
	private static final String NOMBRE_CLIENTE = "JAVIER S. NARANJO H.";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, new Date());

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		Assert.assertEquals(NOMBRE_CLIENTE, repositorioGarantia.obtener(producto.getCodigo()).getNombreCliente());

	}
	
	@Test
	public void garantia200DiasTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(650000).build();
		repositorioProducto.agregar(producto);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, 8-1);
		calendar.set(Calendar.DAY_OF_MONTH, 16);
		Date fechaSolicitud = calendar.getTime();
		
		calendar.set(Calendar.YEAR, 2019);
		calendar.set(Calendar.MONTH, 4-1);
		calendar.set(Calendar.DAY_OF_MONTH, 6);
		Date fechaFinGarantia = calendar.getTime();
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, fechaSolicitud);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertEquals(130000, repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);
		Assert.assertEquals(fechaFinGarantia, repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia());

	}
	
	@Test
	public void garantia100DiasTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conPrecio(450000).build();
		repositorioProducto.agregar(producto);
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, 8-1);
		calendar.set(Calendar.DAY_OF_MONTH, 16);
		Date fechaSolicitud = calendar.getTime();
		
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.MONTH, 12-1);
		calendar.set(Calendar.DAY_OF_MONTH, 11);
		Date fechaFinGarantia = calendar.getTime();
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, fechaSolicitud);

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

		// assert
		Assert.assertEquals(45000, repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(), 0);
		Assert.assertEquals(fechaFinGarantia, repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia());

	}

	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, new Date());

		// act
		vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);;
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
	
	@Test
	public void producto3VocalesTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo(CODIGO_3_VOCALES).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, new Date());

		// act
		try {
			
			vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_3_VOCALES, e.getMessage());
			Assert.assertNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		}
	}
}
