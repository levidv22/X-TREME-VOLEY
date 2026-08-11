package control.referidos.voley.app.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class NotificacionService {

    private final Resend resend;

    public NotificacionService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    public void enviarEmailRecuperacion(String to, String nombreUsuario, String codigo) {
        // Plantilla HTML con diseño Profesional
        String contenidoHtml = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: 'Segoe UI', Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 20px; }
                    .email-card { max-width: 520px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
                    .email-header { background-color: #0f172a; padding: 30px 20px; text-align: center; }
                    .email-header h1 { color: #ffffff; margin: 0; font-size: 22px; font-weight: 700; letter-spacing: 1px; }
                    .email-body { padding: 32px 28px; color: #334155; line-height: 1.6; }
                    .greeting { font-size: 16px; font-weight: 600; color: #0f172a; margin-bottom: 12px; }
                    .code-box { background-color: #f8fafc; border: 2px dashed #cbd5e1; border-radius: 10px; padding: 20px; text-align: center; margin: 24px 0; }
                    .code-number { font-size: 36px; font-weight: 800; color: #0f172a; letter-spacing: 8px; margin: 0; }
                    .expire-text { font-size: 13px; color: #64748b; margin-top: 8px; }
                    .email-footer { background-color: #f8fafc; border-top: 1px solid #e2e8f0; padding: 18px; text-align: center; font-size: 12px; color: #94a3b8; }
                </style>
            </head>
            <body>
                <div class="email-card">
                    <div class="email-header">
                        <h1>CORPORACIÓN X-TREME</h1>
                    </div>
                    <div class="email-body">
                        <p class="greeting">¡Hola, %s!</p>
                        <p>Hemos recibido una solicitud para restablecer tu contraseña. Utiliza el siguiente código de verificación para completar el proceso:</p>
                        
                        <div class="code-box">
                            <p class="code-number">%s</p>
                            <p class="expire-text">Este código expira en <strong>5 minutos</strong>.</p>
                        </div>

                        <p style="font-size: 13px; color: #64748b;">Si no solicitaste este cambio, puedes ignorar este mensaje de manera segura. Tu contraseña seguirá siendo la misma.</p>
                    </div>
                    <div class="email-footer">
                        &copy; 2026 Corporación X-TREME. Todos los derechos reservados.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario != null ? nombreUsuario : "Estimado(a) Cliente", codigo);

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    // Si estás en pruebas con la API Key gratuita por defecto de Resend,
                    // el "from" debe ser "onboarding@resend.dev"
                    .from("Corporación X-TREME <onboarding@resend.dev>")
                    .to(to)
                    .subject("Código de Verificación - Corporación X-TREME")
                    .html(contenidoHtml)
                    .build();

            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Email enviado exitosamente a través de Resend API ID: " + data.getId());
        } catch (ResendException e) {
            System.err.println("Error al enviar email mediante Resend: " + e.getMessage());
            throw new RuntimeException("No se pudo entregar el correo de verificación.", e);
        }
    }
}