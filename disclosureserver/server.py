import socket
import json

class Server:
    def __init__(self):
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        self.ip = socket.gethostbyname(socket.gethostname())
        self.port = 8000
        self.s.bind((self.ip, self.port))
        self.s.listen()
        
        print(f"Listening... {ip}:{port}")

def extract_json_data_from_clientreq(full_string:str):
    full_string = full_string.replace("{START_BLOCK}", "")
    full_string = full_string.replace("{END_BLOCK}", "")
    data = json.dumps(full_string)
    print(f"Recieved: {data}")

    return data

while True:
    cli, addr = s.accept()
    connected = True
    data = ""
    print("connection recieved!")

    while connected:
        print(data)
        try:
            data_chunk = cli.recv(8)
            if data_chunk == b"":
                connected = False

            data += data_chunk.decode()
            
            
            if "{END_BLOCK}" in data:
                get_json_data(data)
                connected = False
                break
                
        except Exception as e:
            print(e)
            connected = False
            print("broken")
            break

    print("Closing connection with client...")
    cli.close()

