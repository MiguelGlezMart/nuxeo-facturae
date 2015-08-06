#nuxeo-facturae

Este plugin permite crear un tipo documental llamado "facturae" para la subida de facturas electrónicas en el formato español según http://www.facturae.gob.es/. Previamente, el usuario tiene que introducir un título al documento y añadir un fichero xml del que se van a extraer los metadatos necesarios para la factura electrónica.

Este plugin contribuye una operación mediante la cual el archivo xml es leído, los metadatos son recogidos y los campos de la factura electrónica son rellenados con dichos metadatos.

#Instalación

Hay dos formas de poner en funcionamiento el plugin:

a. Descargar y compilar el archivo "pom.xml" utilizando Maven y desplegar el plugin. Para ello, se hace uso de la siguiente secuencia de comandos:

	cd nuxeo-facturae-master
	mvn clean install
	cp target/Facturae-*.jar $NUXEO_HOME/nxserver/plugins

b. Abrir un proyecto en Eclipse e incluir el plugin. Posteriormente, seleccionar la opción Nuxeo->Export JAR y copiar el .jar generado en la carpeta de plugins de la distribución de Nuxeo.

Por último, una vez cargado el plugin, es necesario reiniciar el servidor de Nuxeo y ya se puede disfrutar del plugin.

#Uso

Para hacer uso del plugin únicamente hay que seguir los siguientes sencillos pasos:

1. En la plataforma Nuxeo, en el espacio de trabajo, seleccionar la opción de crear un nuevo documento.

2. En la ventana que se abre, escoger el tipo documental llamado "facturae" que se encuentra en la categoría denominada "Misceláneo".

3. Posteriormente, hay que introducir un título al documento y subir el archivo xml que contiene los campos y los metadatos asociados a dichos campos de la factura electrónica.

4. Una vez realizado el punto anterior, hay que pulsar el botón "Crear" y automáticamente se creará la factura electrónica y se mostrarán todos los metadatos contenidos en el fichero xml introducido.

5. Si se desea modificar o añadir algún metadato hay que seleccionar la pestaña "Modificar" y, seguidamente, una vez añadido o modificado los metadatos que se deseen, hay que pulsar el botón "Guardar". En la pestaña "Sumario" aparecerá la información actualizada de la factura electrónica.

