const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = {
  '/api/*': {
    target: 'http://localhost:8080',
    secure: false,
    changeOrigin: true,
    logLevel: 'debug'
  }
};