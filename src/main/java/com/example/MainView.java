
package com.example;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gestión CRUD - Sistema de Inventario")
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();

        tabs.add("Productos", crearSeccion(
                "Código", "Producto", "Descripción", "Precio"));

        tabs.add("Categorías", crearSeccion(
                "ID", "Nombre", "Descripción"));

        tabs.add("Proveedores", crearSeccion(
                "ID", "Nombre", "Contacto", "Teléfono", "Email", "Dirección"));

        tabs.add("Usuarios", crearSeccion(
                "ID", "Nombre", "Apellido", "Email",
                "Teléfono", "Dirección", "Rol", "Estado"));

        add(new H2("Sistema de Inventario - CRUD"), tabs);
    }

    private Component crearSeccion(String... nombresCampos) {

        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        List<String[]> registros = new ArrayList<>();

        TextField[] campos = new TextField[nombresCampos.length];

        for (int i = 0; i < nombresCampos.length; i++) {
            campos[i] = new TextField(nombresCampos[i]);
        }

        FormLayout form = new FormLayout();
        form.add(campos);

        Grid<String[]> grid = new Grid<>();
        grid.setWidthFull();

        for (int i = 0; i < nombresCampos.length; i++) {
            final int columna = i;
            grid.addColumn(fila -> fila[columna])
                    .setHeader(nombresCampos[i])
                    .setAutoWidth(true);
        }

        grid.setItems(registros);

        grid.asSingleSelect().addValueChangeListener(event -> {
            String[] seleccionado = event.getValue();

            if (seleccionado != null) {
                for (int i = 0; i < campos.length; i++) {
                    campos[i].setValue(seleccionado[i]);
                }
            }
        });

        Button btnCrear = new Button("Crear", event -> {

            String[] nuevo = obtenerDatos(campos);

            if (nuevo[0].trim().isEmpty()) {
                Notification.show("El ID o código es obligatorio");
                return;
            }

            if (buscarRegistro(registros, nuevo[0]) != null) {
                Notification.show("Ese ID o código ya existe");
                return;
            }

            registros.add(nuevo);
            grid.setItems(registros);

            Notification.show("Registro creado correctamente");
        });

        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", event -> {

            String id = campos[0].getValue().trim();

            String[] encontrado = buscarRegistro(registros, id);

            if (encontrado != null) {
                grid.asSingleSelect().setValue(encontrado);
                Notification.show("Registro encontrado");
            } else {
                Notification.show("No se encontró el registro");
            }
        });

        Button btnActualizar = new Button("Actualizar", event -> {

            String id = campos[0].getValue().trim();

            String[] encontrado = buscarRegistro(registros, id);

            if (encontrado == null) {
                Notification.show("No se encontró el registro");
                return;
            }

            String[] nuevosDatos = obtenerDatos(campos);

            int posicion = registros.indexOf(encontrado);
            registros.set(posicion, nuevosDatos);

            grid.setItems(registros);
            Notification.show("Registro actualizado correctamente");
        });

        Button btnEliminar = new Button("Eliminar", event -> {

            String id = campos[0].getValue().trim();

            String[] encontrado = buscarRegistro(registros, id);

            if (encontrado == null) {
                Notification.show("No se encontró el registro");
                return;
            }

            registros.remove(encontrado);
            grid.setItems(registros);

            limpiar(campos);
            grid.asSingleSelect().clear();

            Notification.show("Registro eliminado correctamente");
        });

        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", event -> {
            limpiar(campos);
            grid.asSingleSelect().clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(
                btnCrear, btnConsultar, btnActualizar,
                btnEliminar, btnLimpiar
        );

        acciones.getStyle().set("flex-wrap", "wrap");

        layout.add(form, acciones, grid);

        return layout;
    }

    private String[] obtenerDatos(TextField[] campos) {

        String[] datos = new String[campos.length];

        for (int i = 0; i < campos.length; i++) {
            datos[i] = campos[i].getValue().trim();
        }

        return datos;
    }

    private String[] buscarRegistro(List<String[]> registros, String id) {

        for (String[] registro : registros) {
            if (registro[0].equals(id)) {
                return registro;
            }
        }

        return null;
    }

    private void limpiar(TextField[] campos) {

        for (TextField campo : campos) {
            campo.clear();
        }
    }
}
