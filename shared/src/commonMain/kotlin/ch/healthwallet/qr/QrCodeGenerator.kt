package ch.healthwallet.qr

import qrcode.QRCode
import qrcode.color.Colors
import qrcode.render.QRCodeGraphics

fun generateQRCode(data: String): ByteArray {
    val qrCode = QRCode.ofSquares()
        .withColor(Colors.BLACK)
        .withSize(10)
        .build(data)

    val renderedCode: QRCodeGraphics = qrCode.render()
    return renderedCode.getBytes()
}