package dominio;

import dominio.repositorio.RepositorioProducto;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_TIENE_3_VOCALES = "Este producto no cuenta con garantía extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;
    
    private Date fechaActual;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia, Date fechaActual) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;
        this.fechaActual = fechaActual;

    }

    public void generarGarantia(String codigo, String nombreCliente) {
    	
    	int contadorVocales = 0;
    	for(int i=0; i<codigo.length(); i++) {
    		if((codigo.charAt(i)=='a') || (codigo.charAt(i)=='e') || (codigo.charAt(i)=='i') || (codigo.charAt(i)=='o') || (codigo.charAt(i)=='u')
    				 || (codigo.charAt(i)=='A') || (codigo.charAt(i)=='E') || (codigo.charAt(i)=='I') || (codigo.charAt(i)=='O') || (codigo.charAt(i)=='U')){
    			contadorVocales++;
    		}
    	}
    	
    	if(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo)!=null) {
			throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
    	}else if(contadorVocales == 3) {
    		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_3_VOCALES);
    	}else if(repositorioProducto.obtenerPorCodigo(codigo).getPrecio()>500000) {
    		double precioG = repositorioProducto.obtenerPorCodigo(codigo).getPrecio() * 0.2;
    		
    		Calendar calendar = Calendar.getInstance();
    		calendar.setTime(fechaActual);
    		for(int i=0; i<200; i++) {
    			calendar.add(Calendar.DATE, 1);
    			if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) {
    				calendar.add(Calendar.DATE, 1);
    			}
    		}
    		if(calendar.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
    			calendar.add(Calendar.DATE, 1);
			}
    		Date fechaFinGarantia = calendar.getTime();
    		
    		repositorioGarantia.agregar(new GarantiaExtendida(repositorioProducto.obtenerPorCodigo(codigo), fechaActual, fechaFinGarantia, precioG, nombreCliente));
    	}else {
    		double precioGarantia = repositorioProducto.obtenerPorCodigo(codigo).getPrecio() * 0.1;
    		
    		Calendar calend = Calendar.getInstance();
    		calend.setTime(fechaActual);
    		for(int i=0; i<100; i++) {
    			calend.add(Calendar.DATE, 1);
    			if(calend.get(Calendar.DAY_OF_WEEK)==Calendar.MONDAY) {
    				calend.add(Calendar.DATE, 1);
    			}
    		}
    		if(calend.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY) {
    			calend.add(Calendar.DATE, 1);
			}
    		Date fechaFinGarantia = calend.getTime();
    		
    		repositorioGarantia.agregar(new GarantiaExtendida(repositorioProducto.obtenerPorCodigo(codigo), fechaActual, fechaFinGarantia, precioGarantia, nombreCliente));
    	}
    }

    public boolean tieneGarantia(String codigo) {
        if(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo)!=null) {
        	return true;
        }else {
        	return false;
        }
    }

}
