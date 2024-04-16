from json import JSONDecodeError

import threading
import socket

class ClientManager(Threading.thread):
    def __init__(self, cli:socket.socket, on_recv_data_cb):
        self.on_recv_data_cb = on_recv_data_cb
        threading.Thread.__init__(self)
        self.cli = cli
        self.connected = True

    def on_client_disconnect(self):
        pass

    def run(self):
        while self.connected:
            try:
                data_chunk = cli.recv(8)
                if data_chunk == b"":
                    connected = False

                data += data_chunk.decode()
                
                
                if "{END_BLOCK}" in data:
                    extract_json_data_from_block(data)
                    self.on_recv_data_cb(data)
                    connected = False
                    break

            except JSONDecodeError:
                print("Failed to decode python. consult devs")
            except Exception as e:
                print(e.with_traceback())
            
