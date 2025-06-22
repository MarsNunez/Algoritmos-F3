package com.warehouse;

import com.warehouse.util.GraphExport;
import com.warehouse.model.Product;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        WarehouseService svc = new WarehouseService();

        /* ──────────── NODOS ──────────── */
        svc.addLocation(1,  "RECEPCION");
        svc.addLocation(2,  "DESPACHO");

        // Pasillo A
        svc.addLocation(10, "A-1");
        svc.addLocation(11, "A-2");
        svc.addLocation(12, "A-3");
        svc.addLocation(13, "A-4");

        // Pasillo B
        svc.addLocation(20, "B-1");
        svc.addLocation(21, "B-2");
        svc.addLocation(22, "B-3");
        svc.addLocation(23, "B-4");

        /* ──────────── ARISTAS ──────────── */
        // 1) Conexiones horizontales (izq-der)
        //   Recepción → A-1 → A-2 → A-3 → A-4 → Despacho
        svc.connect(1, 10, 7.0);
        svc.connect(10,11,2.4);
        svc.connect(11,12,0.2);
        svc.connect(12,13,3.9);
        svc.connect(13, 2, 4.0);

        //   Recepción → B-1 → B-2 → B-3 → B-4 → Despacho
        svc.connect(1, 20, 4.0);
        svc.connect(20,21,0.4);
        svc.connect(21,22,1.8);
        svc.connect(22,23,10.4);
        svc.connect(23, 2, 4.0);

        // 2) Conexiones verticales (cruces A↔B)
        svc.connect(10,20,1.0);
        svc.connect(12,22,2.0);
        svc.connect(13,23,3.0);

        /* ──────────── INVENTARIO DE EJEMPLO ──────────── */
        svc.addProduct(10, new Product("SKU-100", "Taladro",40));
        svc.addProduct(11, new Product("SKU-999", "Cubo",10));
        svc.addProduct(10, new Product("SKU-101", "Matamoscas",2));
        svc.addProduct(10, new Product("SKU-102", "algo",2));
        svc.addProduct(10, new Product("SKU-103", "algo mas",20));
        svc.addProduct(12, new Product("SKU-200", "Martillo", 60));
        svc.addProduct(12, new Product("SKU-201", "Martillo 2", 60));
        svc.addProduct(21, new Product("SKU-300", "Vidrio.", 120));
        svc.addProduct(23, new Product("SKU-301", "Cemento", 25));
//        svc.addProduct(23, new Product("SKU-301", "Cemento", 1));

        /* ──────────── ACTUALIZAR EL PESO DE UNA ARISTA ──────────── */
        svc.updateConnection(10, 20, 222.0);

        /* ──────────── CALCULAR EL CAMINO MAS CORTO ──────────── */
        svc.getGraph().printShortestPath(12, 2);

        /* ──────────── ANADIR O QUITAR STOCK ──────────── */
        // Sumar 15 unidades del taladro en el estante A-1
        boolean ok = svc.addStock(10, "SKU-100", 15);
        System.out.println(ok ? "Stock actualizado" : "SKU no encontrado");

        // Intentar retirar 10 unidades
        if (svc.removeStock(10, "SKU-100", 5))
            System.out.println("Salida registrada");
        else
            System.out.println("No hay suficiente stock o SKU inexistente");


        /* ──────────── EXPORTAR A DOT ──────────── */
        String dot = GraphExport.toDot(svc.getGraph());
        Files.writeString(Path.of("almacen.dot"), dot);
        System.out.println("→ Generado almacen.dot con topología grande");
    }
}
