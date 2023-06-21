//
// Este archivo ha sido generado por Eclipse Implementation of JAXB v2.3.7 
// Visite https://eclipse-ee4j.github.io/jaxb-ri 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.06.21 a las 04:47:40 PM CLT 
//


package cl.duoc.portafolio.dto.v10.feriavirtual;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}UsuarioType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="direcciones" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}DireccionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="vehiculos" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}VehiculoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="propiedades" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}PropiedadType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="archivos" type="{http://duoc.cl/portafolio/dto/v10/FeriaVirtual}ArchivoUsuarioType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "direcciones",
    "vehiculos",
    "propiedades",
    "archivos"
})
@XmlRootElement(name = "OutputUsuarioObtener")
public class OutputUsuarioObtener
    extends UsuarioType
{

    protected List<DireccionType> direcciones;
    protected List<VehiculoType> vehiculos;
    protected List<PropiedadType> propiedades;
    protected List<ArchivoUsuarioType> archivos;

    /**
     * Gets the value of the direcciones property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the direcciones property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDirecciones().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DireccionType }
     * 
     * 
     */
    public List<DireccionType> getDirecciones() {
        if (direcciones == null) {
            direcciones = new ArrayList<DireccionType>();
        }
        return this.direcciones;
    }

    /**
     * Gets the value of the vehiculos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vehiculos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVehiculos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VehiculoType }
     * 
     * 
     */
    public List<VehiculoType> getVehiculos() {
        if (vehiculos == null) {
            vehiculos = new ArrayList<VehiculoType>();
        }
        return this.vehiculos;
    }

    /**
     * Gets the value of the propiedades property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propiedades property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropiedades().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropiedadType }
     * 
     * 
     */
    public List<PropiedadType> getPropiedades() {
        if (propiedades == null) {
            propiedades = new ArrayList<PropiedadType>();
        }
        return this.propiedades;
    }

    /**
     * Gets the value of the archivos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the archivos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArchivos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArchivoUsuarioType }
     * 
     * 
     */
    public List<ArchivoUsuarioType> getArchivos() {
        if (archivos == null) {
            archivos = new ArrayList<ArchivoUsuarioType>();
        }
        return this.archivos;
    }

}
