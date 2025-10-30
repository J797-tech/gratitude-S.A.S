# Restaurante - Gratitude

Este es un sitio web para un restaurante especializado en postres, heladería, panadería y productos gourmet. Incluye funcionalidades para navegar productos, gestionar un carrito de compras, hacer pedidos para recoger y reservar mesas.

## Características

- **Navegación de Productos**: Explora categorías como Repostería, Heladería, Panadería y Gourmet.
- **Carrito de Compras**: Agrega productos, ajusta cantidades y realiza pedidos para recoger.
- **Reserva de Mesas**: Modal mejorado para reservar mesas con validación de formulario y retroalimentación visual.
- **Gestión de Usuarios y Pedidos**: Páginas administrativas para gestionar usuarios, pedidos y platos (requieren autenticación).

## Tecnologías Utilizadas

- **HTML5**: Estructura del sitio.
- **CSS3**: Estilos personalizados con variables CSS para colores verdes.
- **JavaScript**: Lógica del frontend, incluyendo manejo del carrito y formularios.
- **Bootstrap 5**: Framework CSS para diseño responsivo y componentes como modales y offcanvas.

## Estructura del Proyecto

```
Restaurante/
├── index.html                 # Página principal
├── reservar.html              # Página de reserva de mesas
├── favoritos.html             # Página de productos favoritos
├── historial_pedidos.html     # Historial de pedidos del usuario
├── contactanos.html           # Página de contacto
├── gestion_usuarios.html      # Gestión de usuarios (admin)
├── gestion_pedidos.html       # Gestión de pedidos (mesero/admin)
├── gestion_platos.html        # Gestión de platos (cocinero/admin)
├── css/
│   ├── style.css              # Estilos principales
│   └── toast.css              # Estilos para notificaciones toast
├── js/
│   ├── script.js              # Lógica principal del sitio
│   └── login.js               # Manejo de autenticación simulada
├── images/                    # Imágenes del proyecto
│   ├── logo gratitude.jpg
│   └── cafe_gourmet.jpg
├── .vscode/                   # Configuración de VSCode
│   └── settings.json
├── .gitignore                 # Archivos ignorados por Git
├── README.md                  # Este archivo
└── TODO.md                    # Lista de tareas pendientes
```

## Instalación y Uso

1. Clona el repositorio:
   ```bash
   git clone <url-del-repositorio>
   cd Restaurante
   ```

2. Abre `index.html` en un navegador web.

3. Navega por los productos, agrega al carrito y prueba la reserva de mesa.

Nota: Este es un proyecto de demostración. En producción, conecta a un backend para autenticación y base de datos.

## Contribución

Si deseas contribuir:
1. Haz un fork del repositorio.
2. Crea una rama para tu feature: `git checkout -b feature/nueva-funcionalidad`.
3. Haz commit de tus cambios: `git commit -m 'Agrega nueva funcionalidad'`.
4. Push a la rama: `git push origin feature/nueva-funcionalidad`.
5. Abre un Pull Request.


