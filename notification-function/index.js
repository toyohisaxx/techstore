exports.handler = async (event) => {
    for (const record of event.Records) {
        const body = JSON.parse(record.body);
        console.log("=====================================================");
        console.log("[Lambda] NUEVA AUDITORIA RECIBIDA DESDE SQS");
        console.log("=====================================================");
        console.log("Accion    : " + body.accion);
        console.log("Producto  : " + body.nombre + " (ID: " + body.productoId + ")");
        console.log("Usuario   : " + body.usuario);
        console.log("Fecha     : " + body.fecha);
        console.log("=====================================================");
    }
    return { statusCode: 200, body: "OK" };
};