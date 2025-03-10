const express = require('express');
const mysql = require('mysql2');
const bcrypt = require('bcrypt');
const bodyParser = require('body-parser');
const multer = require('multer');
const path = require('path');

const app = express();
const saltRounds = 10; // Número de saltos para el cifrado bcrypt

// Configuración de body-parser para recibir datos JSON en las solicitudes
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// Configuración para servir archivos estáticos en la carpeta "uploads"
app.use('/uploads', express.static('uploads'));

// Configuración de multer para almacenar imágenes en la carpeta "uploads"
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, uniqueSuffix + path.extname(file.originalname));
    }
});
const upload = multer({ storage: storage });

// Configuración de la conexión a la base de datos MySQL
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root', // Cambia a tu usuario de MySQL
    password: 'Pineda1345', // Cambia a tu contraseña de MySQL
    database: 'Motos'
});

db.connect((err) => {
    if (err) {
        console.error('Error al conectar a la base de datos:', err);
        return;
    }
    console.log('Conexión exitosa a la base de datos MySQL.');
});

// Ruta para registrar un nuevo usuario
app.post('/register', (req, res) => {
    const { nombre_usuario, correo, contrasena, edad } = req.body;

    if (!nombre_usuario || !correo || !contrasena || !edad) {
        return res.status(400).json({ error: 'Por favor, completa todos los campos.' });
    }

    bcrypt.hash(contrasena, saltRounds, (err, hash) => {
        if (err) {
            return res.status(500).json({ error: 'Error en el servidor.' });
        }

        const query = 'INSERT INTO usuarios (nombre_usuario, correo, contrasena, edad) VALUES (?, ?, ?, ?)';
        db.query(query, [nombre_usuario, correo, hash, edad], (err, result) => {
            if (err) {
                return res.status(500).json({ error: 'Error al registrar el usuario.' });
            }
            res.json({ message: 'Usuario registrado correctamente.' });
        });
    });
});

// Ruta para iniciar sesión
app.post('/login', (req, res) => {
    const { correo, contrasena } = req.body;

    if (!correo || !contrasena) {
        return res.status(400).json({ error: 'Por favor, completa todos los campos.' });
    }

    const query = 'SELECT * FROM usuarios WHERE correo = ?';
    db.query(query, [correo], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Error en el servidor.' });
        }

        if (results.length === 0) {
            return res.status(404).json({ error: 'Usuario no encontrado.' });
        }

        const usuario = results[0];

        bcrypt.compare(contrasena, usuario.contrasena, (err, result) => {
            if (err) {
                return res.status(500).json({ error: 'Error en el servidor.' });
            }

            if (result) {
                // Enviar el user_id junto con el mensaje de éxito
                res.json({
                    message: 'Inicio de sesión exitoso.',
                    user_id: usuario.id, // Asegúrate de que 'id' sea el nombre del campo de ID en tu tabla
                    nombreUsuario: usuario.nombre_usuario,
                    correo: usuario.correo,
                    edad: usuario.edad
                });
            } else {
                res.status(401).json({ error: 'Contraseña incorrecta.' });
            }
        });
    });
});


// Ruta para obtener detalles del usuario por correo electrónico
app.get('/user/:email', (req, res) => {
    const correo = req.params.email;
    const query = 'SELECT nombre_usuario, correo, edad FROM usuarios WHERE correo = ?';
    db.query(query, [correo], (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Error al obtener los datos del usuario.' });
        }
        if (results.length === 0) {
            return res.status(404).json({ error: 'Usuario no encontrado.' });
        }
        res.json(results[0]);
    });
});

// Ruta para crear un nuevo post con una imagen
app.post('/posts', upload.single('image'), (req, res) => {
    const { title, description, user_id } = req.body;
    const imagePath = req.file ? `/uploads/${req.file.filename}` : null;

    // Log para verificar los datos recibidos
    console.log("Datos recibidos en el cuerpo de la solicitud:");
    console.log("title:", title);
    console.log("description:", description);
    console.log("user_id:", user_id);
    console.log("imagePath:", imagePath);

    if (!title || !description || !user_id || !imagePath) {
        return res.status(400).json({ error: 'Por favor, completa todos los campos y proporciona una imagen.' });
    }

    const query = 'INSERT INTO posts (title, description, image, user_id) VALUES (?, ?, ?, ?)';
    db.query(query, [title, description, imagePath, user_id], (err, result) => {
        if (err) {
            console.error("Error al crear el post:", err); // Log para errores
            return res.status(500).json({ error: 'Error al crear el post.' });
        }
        res.json({ message: 'Post creado correctamente.' });
    });
});



// Ruta para obtener todos los posts
app.get('/posts', (req, res) => {
    const query = `
        SELECT posts.id, posts.title, posts.description, posts.image, posts.date, posts.likes, usuarios.nombre_usuario AS profile_name
        FROM posts
        JOIN usuarios ON posts.user_id = usuarios.id
    `;

    db.query(query, (err, results) => {
        if (err) {
            return res.status(500).json({ error: 'Error al obtener los posts.' });
        }

        // Mostrar los datos en la consola del servidor
        console.log('Datos obtenidos:', results);

        // Enviar la respuesta al cliente
        res.json(results);
    });
});

// Ruta para obtener las etiquetas de un post específico
app.get('/posts/:postId/etiquetas', (req, res) => {
    const postId = req.params.postId;
    const query = `
        SELECT etiquetas.id, etiquetas.nombre_etiqueta
        FROM etiquetas
        JOIN post_etiquetas ON etiquetas.id = post_etiquetas.etiqueta_id
        WHERE post_etiquetas.post_id = ?
    `;

    db.query(query, [postId], (err, results) => {
        if (err) {
            console.error("Error al obtener las etiquetas:", err);
            return res.status(500).json({ error: 'Error al obtener las etiquetas del post.' });
        }
        if (results.length === 0) {
            return res.status(404).json({ message: 'No se encontraron etiquetas para este post.' });
        }
        res.json(results);
    });
});

// Ruta para asignar etiquetas a un post
app.post('/posts/:postId/tags', (req, res) => {
    const postId = req.params.postId;
    const { etiquetaIds } = req.body; // Lista de IDs de etiquetas

    if (!etiquetaIds || !Array.isArray(etiquetaIds) || etiquetaIds.length === 0) {
        return res.status(400).json({ error: 'Debe proporcionar una lista de etiquetas' });
    }

    // Verificar etiquetas ya asignadas
    const checkQuery = 'SELECT etiqueta_id FROM post_etiquetas WHERE post_id = ? AND etiqueta_id IN (?)';
    db.query(checkQuery, [postId, etiquetaIds], (err, results) => {
        if (err) {
            console.error("Error al verificar etiquetas:", err);
            return res.status(500).json({ error: 'Error al verificar etiquetas' });
        }

        const alreadyAssignedIds = results.map(row => row.etiqueta_id);
        const newIds = etiquetaIds.filter(id => !alreadyAssignedIds.includes(id));

        if (alreadyAssignedIds.length > 0 && newIds.length === 0) {
            // Si todas las etiquetas ya están asignadas, devolver 409
            return res.status(409).json({ message: 'Algunas etiquetas ya estaban asignadas', alreadyAssigned: alreadyAssignedIds });
        } else if (newIds.length > 0) {
            // Insertar solo las etiquetas nuevas
            const values = newIds.map(etiquetaId => [postId, etiquetaId]);
            const insertQuery = 'INSERT INTO post_etiquetas (post_id, etiqueta_id) VALUES ?';
            db.query(insertQuery, [values], (insertErr) => {
                if (insertErr) {
                    console.error("Error al asignar etiquetas:", insertErr);
                    return res.status(500).json({ error: 'Error al asignar etiquetas al post.' });
                }
                res.json({ message: 'Etiquetas asignadas correctamente', newlyAssigned: newIds, alreadyAssigned: alreadyAssignedIds });
            });
        } else {
            res.json({ message: 'No se asignaron nuevas etiquetas' });
        }
    });
});

// Ruta para obtener todas las etiquetas disponibles
app.get('/tags', (req, res) => {
    const query = 'SELECT id, nombre_etiqueta FROM etiquetas';

    db.query(query, (err, results) => {
        if (err) {
            console.error("Error al obtener etiquetas:", err);
            return res.status(500).json({ error: 'Error al obtener etiquetas' });
        }
        res.json(results);
    });
});

// Ruta para eliminar etiquetas asignadas a un post
app.delete('/posts/:postId/tags', (req, res) => {
    const postId = req.params.postId;
    const { etiquetaIds } = req.body; // Lista de IDs de etiquetas a eliminar

    if (!etiquetaIds || !Array.isArray(etiquetaIds) || etiquetaIds.length === 0) {
        return res.status(400).json({ error: 'Debe proporcionar una lista de etiquetas a eliminar' });
    }

    // Consulta SQL para eliminar las etiquetas específicas de un post
    const query = 'DELETE FROM post_etiquetas WHERE post_id = ? AND etiqueta_id IN (?)';

    db.query(query, [postId, etiquetaIds], (err, result) => {
        if (err) {
            console.error("Error al eliminar etiquetas:", err);
            return res.status(500).json({ error: 'Error al eliminar etiquetas del post.' });
        }

        // Verificar si se eliminaron etiquetas
        if (result.affectedRows > 0) {
            res.json({ message: 'Etiquetas eliminadas correctamente del post.' });
        } else {
            res.status(404).json({ message: 'No se encontraron etiquetas para eliminar o ya estaban eliminadas.' });
        }
    });
});

app.get('/posts/:postId/comments', (req, res) => {
    const postId = req.params.postId;

    const query = `
        SELECT comments.id, comments.comment_text, comments.created_at, 
               usuarios.nombre_usuario 
        FROM comments 
        JOIN usuarios ON comments.usuario_id = usuarios.id 
        WHERE comments.post_id = ? 
        ORDER BY comments.created_at DESC
    `;

    db.query(query, [postId], (error, comments) => {
        if (error) {
            console.error(error);
            return res.status(500).json({ error: 'Error al recuperar los comentarios' });
        }
        
        // Mostrar los comentarios en la consola antes de enviarlos
        console.log("Comentarios recuperados:", comments);
        
        res.status(200).json(comments);
    });
});

// Ruta para agregar un nuevo comentario a un post
app.post('/posts/:postId/comments', (req, res) => {
    const postId = req.params.postId;
    const { comment_text, usuario_id } = req.body;

    // Verificar que se reciban los campos requeridos
    if (!comment_text || !usuario_id) {
        return res.status(400).json({ error: 'Debe proporcionar el texto del comentario y el ID del usuario.' });
    }

    const query = 'INSERT INTO comments (post_id, comment_text, usuario_id) VALUES (?, ?, ?)';
    
    db.query(query, [postId, comment_text, usuario_id], (err, result) => {
        if (err) {
            console.error("Error al agregar el comentario:", err);
            return res.status(500).json({ error: 'Error al agregar el comentario al post.' });
        }
        console.log(`Comentario Correctamente asignado por ${usuario_id}`)
        res.json({ message: 'Comentario agregado correctamente.', commentId: result.insertId });
    });
});

app.get('/ping',(req,res)=>{
    console.log("Pong")
    res.send('Servidor ejecutandose')
})

// Iniciar el servidor en el puerto 3000
app.listen(3000, '0.0.0.0', () => {
    console.log('Servidor ejecutándose en http://0.0.0.0:3000');
});
