from clientworker import ClientHandler
from block import BlockPacket, BlockType
from typing import Dict

import socket
import json
import uuid

class ServerHandler:
    instance = None

    def __init__(self):
        if self.instance is not None:
            print("Trying to create another server handler?")
            pass    

        self.instance = self
        self.clients = Dict[uuid.UUID, ClientHandler]
        self.s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
        self.ip = socket.gethostbyname(socket.gethostname())
        self.port = 8000
        self.s.bind((self.ip, self.port))
        self.s.listen()
        
        print(f"Listening... {ip}:{port}")

    def run(self):
        while True:
            cli, addr = self.s.accept()
            id = uuid.UUID()
            client_obj = ClientHandler(self, id, cli, addr)
            client_obj.run()


            client_obj.request_public_key()
            if client_obj.public_key == None:
                print("Failed to get public key from client.")
                continue

            self.relay_block_to_clients(BlockPacket.create_block_packet(
                block_type=BlockType.SVR_RES_NEW_CONNECTION,
                data={"id":id, "public_key":client_obj.public_key}
            ))

            self.clients[id] = client_obj


    def relay_block_to_clients(self, block:BlockPacket):
        for c in self.clients:
            c.send_block(block)


    def send_block_to_client(self, id:uuid.UUID, block:BlockPacket):
        cli:ClientHandler = self.clients.get(id)
        cli.send_block(block)

