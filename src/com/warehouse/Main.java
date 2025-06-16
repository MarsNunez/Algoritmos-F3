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

        // Pasillo A (fila superior)
        svc.addLocation(10, "A-1");
        svc.addLocation(11, "A-2");
        svc.addLocation(12, "A-3");
        svc.addLocation(13, "A-4");

        // Pasillo B (fila inferior)
        svc.addLocation(20, "B-1");
        svc.addLocation(21, "B-2");
        svc.addLocation(22, "B-3");
        svc.addLocation(23, "B-4");

        /* ──────────── ARISTAS ──────────── */
        // 1) Conexiones horizontales (izq-der)
        //   Recepción → A-1 → A-2 → A-3 → A-4 → Despacho
        svc.connect(1, 10, 4.0);  svc.connect(10,11,1.4);
        svc.connect(11,12,1.4);   svc.connect(12,13,1.4);
        svc.connect(13, 2, 4.0);

        //   Recepción → B-1 → B-2 → B-3 → B-4 → Despacho
        svc.connect(1, 20, 4.0);  svc.connect(20,21,1.4);
        svc.connect(21,22,1.4);   svc.connect(22,23,1.4);
        svc.connect(23, 2, 4.0);

        // 2) Conexiones verticales (cruces A↔B)
        svc.connect(10,20,1.0);   svc.connect(11,21,1.0);
        svc.connect(12,22,1.0);   svc.connect(13,23,1.0);

        /* ──────────── INVENTARIO DE EJEMPLO ──────────── */
        svc.addProduct(10, new Product("SKU-TAL", "Taladro",      40));
        svc.addProduct(12, new Product("SKU-MAR", "Martillo",     60));
        svc.addProduct(21, new Product("SKU-DES", "Destornill.", 120));
        svc.addProduct(23, new Product("SKU-CEM", "Cemento",      25));

        /* ──────────── EXPORTAR A DOT ──────────── */
        String dot = GraphExport.toDot(svc.getGraph());
        Files.writeString(Path.of("almacen.dot"), dot);
        System.out.println("→ Generado almacen.dot con topología grande");
    }
}
