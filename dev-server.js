const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 5500;
const ROOT = path.join(__dirname, 'frontend');
const ERROR_404 = path.join(ROOT, 'pages', 'errors', '404.html');

const MIME = {
  '.html': 'text/html',
  '.css': 'text/css',
  '.js': 'application/javascript',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon',
};

http.createServer((req, res) => {
  let filePath = path.join(ROOT, decodeURIComponent(req.url));

  // If it's a directory, try index.html inside it
  if (fs.existsSync(filePath) && fs.statSync(filePath).isDirectory()) {
    filePath = path.join(filePath, 'index.html');
  }

  if (fs.existsSync(filePath) && fs.statSync(filePath).isFile()) {
    const ext = path.extname(filePath).toLowerCase();
    res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream' });
    fs.createReadStream(filePath).pipe(res);
  } else {
    // Serve custom 404 page
    res.writeHead(404, { 'Content-Type': 'text/html' });
    fs.createReadStream(ERROR_404).pipe(res);
  }
}).listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
  console.log(`Try a bad URL:  http://localhost:${PORT}/pages/asd`);
  console.log(`Press Ctrl+C to stop`);
});

