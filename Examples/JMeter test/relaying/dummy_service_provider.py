import os
from http.server import BaseHTTPRequestHandler, HTTPServer

hostName = "localhost"
PORT_NUMBER = 13456


class myHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        self.wfile.write(bytes("OK - GET", "utf-8"))
        return 

    def do_POST(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        self.wfile.write(bytes("OK - POST", "utf-8"))
        return


server = HTTPServer(("", PORT_NUMBER), myHandler)
try:
    server.serve_forever()
except KeyboardInterrupt:
    server.socket.close()
    
