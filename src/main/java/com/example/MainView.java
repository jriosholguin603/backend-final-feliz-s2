package com.example;

import java.awt.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Clientes;
import com.example.model.DetallePedido;
import com.example.model.Pedidos;
import com.example.model.Usuario;

@PageTitle("Gestión CRUD - Sistema de Pedidos")
@Route("")
public class MainView extends VerticalLayout {

    private long clienteIdCounter = 1;
    private long pedidoIdCounter = 1;
    private long detalleIdCounter = 1;
    private long usuarioIdCounter = 1;

    public MainView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        TabSheet tabs = new TabSheet();
        tabs.setWidthFull();

        tabs.add("Clientes", crearSeccionClientes());
        tabs.add("Pedidos", crearSeccionPedidos());
        tabs.add("Detalle Pedido", crearSeccionDetallePedido());
        tabs.add("Usuarios", crearSeccionUsuarios());

        add(new H2("Sistema de Gestión - CRUD"), tabs);
    }

    // ==========================================
    // 1. SECCIÓN CLIENTES
    // ==========================================
    private Component crearSeccionClientes() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        List<Clientes> lista = new ArrayList<>();

        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Email");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField estadoField = new TextField("Estado");

        idField.setReadOnly(true); // El ID es autogenerado

        FormLayout form = new FormLayout(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, estadoField);

        Grid<Clientes> grid = new Grid<>(Clientes.class, false);
        grid.addColumn(Clientes::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Clientes::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Clientes::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Clientes::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Clientes::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Clientes::getDireccion).setHeader("Dirección").setAutoWidth(true);
        grid.addColumn(Clientes::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(Clientes::getFechaRegistro).setHeader("Fecha Registro").setAutoWidth(true);

        grid.setItems(lista);

        grid.asSingleSelect().addValueChangeListener(event -> {
            Clientes c = event.getValue();
            if (c != null) {
                idField.setValue(c.getId() != null ? c.getId().toString() : "");
                nombreField.setValue(c.getNombre() != null ? c.getNombre() : "");
                apellidoField.setValue(c.getApellido() != null ? c.getApellido() : "");
                emailField.setValue(c.getEmail() != null ? c.getEmail() : "");
                telefonoField.setValue(c.getTelefono() != null ? c.getTelefono() : "");
                direccionField.setValue(c.getDireccion() != null ? c.getDireccion() : "");
                estadoField.setValue(c.getEstado() != null ? c.getEstado() : "");
            }
        });

        Button btnCrear = new Button("Crear", event -> {
            if (nombreField.isEmpty()) {
                Notification.show("El nombre es obligatorio");
                return;
            }
            Clientes c = new Clientes(
                clienteIdCounter++,
                nombreField.getValue().trim(),
                apellidoField.getValue().trim(),
                emailField.getValue().trim(),
                telefonoField.getValue().trim(),
                direccionField.getValue().trim(),
                estadoField.getValue().trim(),
                LocalDateTime.now()
            );
            lista.add(c);
            grid.setItems(lista);
            limpiarCampos(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, estadoField);
            Notification.show("Cliente creado correctamente");
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", event -> {
            Long id = parseLong(idField.getValue());
            if (id == null) {
                Notification.show("Selecciona o ingresa un ID válido");
                return;
            }
            Clientes encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado != null) {
                grid.asSingleSelect().setValue(encontrado);
                Notification.show("Cliente encontrado");
            } else {
                Notification.show("No se encontró el cliente");
            }
        });

        Button btnActualizar = new Button("Actualizar", event -> {
            Long id = parseLong(idField.getValue());
            Clientes encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("Selecciona un cliente de la tabla para actualizar");
                return;
            }
            encontrado.setNombre(nombreField.getValue().trim());
            encontrado.setApellido(apellidoField.getValue().trim());
            encontrado.setEmail(emailField.getValue().trim());
            encontrado.setTelefono(telefonoField.getValue().trim());
            encontrado.setDireccion(direccionField.getValue().trim());
            encontrado.setEstado(estadoField.getValue().trim());

            grid.setItems(lista);
            Notification.show("Cliente actualizado correctamente");
        });

        Button btnEliminar = new Button("Eliminar", event -> {
            Long id = parseLong(idField.getValue());
            Clientes encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("No se encontró el cliente a eliminar");
                return;
            }
            lista.remove(encontrado);
            grid.setItems(lista);
            limpiarCampos(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, estadoField);
            grid.asSingleSelect().clear();
            Notification.show("Cliente eliminado correctamente");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", event -> {
            limpiarCampos(idField, nombreField, apellidoField, emailField, telefonoField, direccionField, estadoField);
            grid.asSingleSelect().clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");

        layout.add(form, acciones, grid);
        return layout;
    }

    // ==========================================
    // 2. SECCIÓN PEDIDOS
    // ==========================================
    private Component crearSeccionPedidos() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        List<Pedidos> lista = new ArrayList<>();

        TextField idField = new TextField("ID");
        TextField clienteIdField = new TextField("ID Cliente");
        TextField totalField = new TextField("Total");
        TextField estadoField = new TextField("Estado");

        idField.setReadOnly(true);

        FormLayout form = new FormLayout(idField, clienteIdField, totalField, estadoField);

        Grid<Pedidos> grid = new Grid<>(Pedidos.class, false);
        grid.addColumn(Pedidos::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Pedidos::getClienteId).setHeader("ID Cliente").setAutoWidth(true);
        grid.addColumn(Pedidos::getFechaPedido).setHeader("Fecha Pedido").setAutoWidth(true);
        grid.addColumn(Pedidos::getTotal).setHeader("Total").setAutoWidth(true);
        grid.addColumn(Pedidos::getEstado).setHeader("Estado").setAutoWidth(true);

        grid.setItems(lista);

        grid.asSingleSelect().addValueChangeListener(event -> {
            Pedidos p = event.getValue();
            if (p != null) {
                idField.setValue(p.getId() != null ? p.getId().toString() : "");
                clienteIdField.setValue(p.getClienteId() != null ? p.getClienteId().toString() : "");
                totalField.setValue(p.getTotal() != null ? p.getTotal().toString() : "");
                estadoField.setValue(p.getEstado() != null ? p.getEstado() : "");
            }
        });

        Button btnCrear = new Button("Crear", event -> {
            Pedidos p = new Pedidos(
                pedidoIdCounter++,
                parseLong(clienteIdField.getValue()),
                LocalDateTime.now(),
                parseBigDecimal(totalField.getValue()),
                estadoField.getValue().trim().isEmpty() ? "pendiente" : estadoField.getValue().trim()
            );
            lista.add(p);
            grid.setItems(lista);
            limpiarCampos(idField, clienteIdField, totalField, estadoField);
            Notification.show("Pedido creado correctamente");
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", event -> {
            Long id = parseLong(idField.getValue());
            Pedidos encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado != null) {
                grid.asSingleSelect().setValue(encontrado);
                Notification.show("Pedido encontrado");
            } else {
                Notification.show("No se encontró el pedido");
            }
        });

        Button btnActualizar = new Button("Actualizar", event -> {
            Long id = parseLong(idField.getValue());
            Pedidos encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("Selecciona un pedido de la tabla");
                return;
            }
            encontrado.setClienteId(parseLong(clienteIdField.getValue()));
            encontrado.setTotal(parseBigDecimal(totalField.getValue()));
            encontrado.setEstado(estadoField.getValue().trim());
            grid.setItems(lista);
            Notification.show("Pedido actualizado correctamente");
        });

        Button btnEliminar = new Button("Eliminar", event -> {
            Long id = parseLong(idField.getValue());
            Pedidos encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("No se encontró el pedido");
                return;
            }
            lista.remove(encontrado);
            grid.setItems(lista);
            limpiarCampos(idField, clienteIdField, totalField, estadoField);
            grid.asSingleSelect().clear();
            Notification.show("Pedido eliminado correctamente");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", event -> {
            limpiarCampos(idField, clienteIdField, totalField, estadoField);
            grid.asSingleSelect().clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");

        layout.add(form, acciones, grid);
        return layout;
    }

    // ==========================================
    // 3. SECCIÓN DETALLE PEDIDO
    // ==========================================
    private Component crearSeccionDetallePedido() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        List<DetallePedido> lista = new ArrayList<>();

        TextField idField = new TextField("ID");
        TextField pedidoIdField = new TextField("ID Pedido");
        TextField productoIdField = new TextField("ID Producto");
        TextField cantidadField = new TextField("Cantidad");
        TextField precioUnitarioField = new TextField("Precio Unitario");
        TextField subtotalField = new TextField("Subtotal");

        idField.setReadOnly(true);

        FormLayout form = new FormLayout(idField, pedidoIdField, productoIdField, cantidadField, precioUnitarioField, subtotalField);

        Grid<DetallePedido> grid = new Grid<>(DetallePedido.class, false);
        grid.addColumn(DetallePedido::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(DetallePedido::getPedidoId).setHeader("ID Pedido").setAutoWidth(true);
        grid.addColumn(DetallePedido::getProductoId).setHeader("ID Producto").setAutoWidth(true);
        grid.addColumn(DetallePedido::getCantidad).setHeader("Cantidad").setAutoWidth(true);
        grid.addColumn(DetallePedido::getPrecioUnitario).setHeader("Precio Unitario").setAutoWidth(true);
        grid.addColumn(DetallePedido::getSubtotal).setHeader("Subtotal").setAutoWidth(true);

        grid.setItems(lista);

        grid.asSingleSelect().addValueChangeListener(event -> {
            DetallePedido d = event.getValue();
            if (d != null) {
                idField.setValue(d.getId() != null ? d.getId().toString() : "");
                pedidoIdField.setValue(d.getPedidoId() != null ? d.getPedidoId().toString() : "");
                productoIdField.setValue(d.getProductoId() != null ? d.getProductoId().toString() : "");
                cantidadField.setValue(d.getCantidad() != null ? d.getCantidad().toString() : "");
                precioUnitarioField.setValue(d.getPrecioUnitario() != null ? d.getPrecioUnitario().toString() : "");
                subtotalField.setValue(d.getSubtotal() != null ? d.getSubtotal().toString() : "");
            }
        });

        Button btnCrear = new Button("Crear", event -> {
            int cant = parseInt(cantidadField.getValue());
            BigDecimal precio = parseBigDecimal(precioUnitarioField.getValue());
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cant));

            DetallePedido d = new DetallePedido(
                detalleIdCounter++,
                parseLong(pedidoIdField.getValue()),
                parseLong(productoIdField.getValue()),
                cant,
                precio,
                subtotal
            );
            lista.add(d);
            grid.setItems(lista);
            subtotalField.setValue(subtotal.toString());
            limpiarCampos(idField, pedidoIdField, productoIdField, cantidadField, precioUnitarioField, subtotalField);
            Notification.show("Detalle creado correctamente");
        });
        btnCrear.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button btnConsultar = new Button("Consultar", event -> {
            Long id = parseLong(idField.getValue());
            DetallePedido encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado != null) {
                grid.asSingleSelect().setValue(encontrado);
                Notification.show("Detalle encontrado");
            } else {
                Notification.show("No se encontró el detalle");
            }
        });

        Button btnActualizar = new Button("Actualizar", event -> {
            Long id = parseLong(idField.getValue());
            DetallePedido encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("Selecciona un detalle de la tabla");
                return;
            }
            int cant = parseInt(cantidadField.getValue());
            BigDecimal precio = parseBigDecimal(precioUnitarioField.getValue());
            
            encontrado.setPedidoId(parseLong(pedidoIdField.getValue()));
            encontrado.setProductoId(parseLong(productoIdField.getValue()));
            encontrado.setCantidad(cant);
            encontrado.setPrecioUnitario(precio);
            encontrado.setSubtotal(precio.multiply(BigDecimal.valueOf(cant)));

            grid.setItems(lista);
            Notification.show("Detalle actualizado correctamente");
        });

        Button btnEliminar = new Button("Eliminar", event -> {
            Long id = parseLong(idField.getValue());
            DetallePedido encontrado = lista.stream().filter(x -> x.getId().equals(id)).findFirst().orElse(null);
            if (encontrado == null) {
                Notification.show("No se encontró el detalle");
                return;
            }
            lista.remove(encontrado);
            grid.setItems(lista);
            limpiarCampos(idField, pedidoIdField, productoIdField, cantidadField, precioUnitarioField, subtotalField);
            grid.asSingleSelect().clear();
            Notification.show("Detalle eliminado correctamente");
        });
        btnEliminar.addThemeVariants(ButtonVariant.LUMO_ERROR);

        Button btnLimpiar = new Button("Limpiar", event -> {
            limpiarCampos(idField, pedidoIdField, productoIdField, cantidadField, precioUnitarioField, subtotalField);
            grid.asSingleSelect().clear();
        });

        HorizontalLayout acciones = new HorizontalLayout(btnCrear, btnConsultar, btnActualizar, btnEliminar, btnLimpiar);
        acciones.getStyle().set("flex-wrap", "wrap");

        layout.add(form, acciones, grid);
        return layout;
    }

    /**
     * @return
     */
    private Component crearSeccionUsuarios() {
        VerticalLayout layout = new VerticalLayout();
        layout.setPadding(false);
        layout.setSizeFull();

        List<Usuario> lista = new ArrayList<>();

        TextField idField = new TextField("ID");
        TextField nombreField = new TextField("Nombre");
        TextField apellidoField = new TextField("Apellido");
        TextField emailField = new TextField("Email");
        TextField telefonoField = new TextField("Teléfono");
        TextField direccionField = new TextField("Dirección");
        TextField rolField = new TextField("Rol");
        TextField estadoField = new TextField("Estado");

        idField.setReadOnly(true);

        Grid<Usuario> grid = new Grid<>(Usuario.class, false);
        grid.addColumn(Usuario::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Usuario::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Usuario::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Usuario::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Usuario::getTelefono).setHeader("Teléfono").setAutoWidth(true);
        grid.addColumn(Usuario::getRol).setHeader("Rol").setAutoWidth(true);
        grid.addColumn(Usuario::getEstado).setHeader("Estado").setAutoWidth(true);
        grid.addColumn(Usuario

    /**
     * 
     */
    private void setSizeFull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void setPadding(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void setSpacing(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static class Grid<T> {

        public Grid() {
        }
    }
