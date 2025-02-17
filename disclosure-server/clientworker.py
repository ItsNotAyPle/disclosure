from json import JSONDecodeError
from block import BlockPacket, BlockType

import time #testing
import threading
import socket
import uuid

# TODO: does addr_info need to be passed?
class ClientHandler(threading.Thread):
    def __init__(self, parent_server_instance, id:uuid.UUID, cli:socket.socket, addr_info=None):
        self.server = parent_server_instance
        threading.Thread.__init__(self, daemon=True)
        self.id = id
        self.public_key = None
        # self.on_recv_data_cb = on_recv_data_cb
        self.addr_info = addr_info
        self.connected = True
        self.cli = cli
        self.request_public_key()

        # self.daemon = True


    def __del__(self):
        self.__on_client_disconnect()

    def __on_client_disconnect(self):
        connected = False
        self.cli.close()

    def send_block(self, block:BlockPacket):
        self.cli.sendall(block.encode())
        pass

    def request_public_key(self):
        self.cli.sendall(BlockPacket.create_block_packet(BlockType.SVR_REQ_PUB_KEY, {}).encode())


    # DO THIS WHEN YOU AWAKE:
    def send_ack(self, blocktype:BlockType):
        # self.cli.sendall(BlockPacket.create_block_packet(BlockType.))
        pass


    def on_recv_data(self, data:str):
        # print("Recieved data: " + data)
        blockdata = BlockPacket(data)
        json_data = blockdata.json_data['data']

        if blockdata.block_type == BlockType.CLI_RES_PUB_KEY:
            self.public_key = json_data['public_key']
            self.send_block(BlockPacket.create_block_packet(BlockType.SVR_RES_RECV_PUB_KEY, None))
            # print(f"Recieved public key from client [{self.public_key}]")
            return

        elif blockdata.block_type == BlockType.MESSAGE:
            # self.server.send_block_to_client(
            #     json_data['to'],
            #     blockdata
            #     )

            print("Recieved message: " + str(json_data['message']))
            return

        

    # TODO: think of smthn better 
    # def recieve_public_key(self):
    #     data = ""

    def listen_for_data_chunk(self) -> str:
        data = ""
        while True:
            data_chunk = self.cli.recv(8)
            if data_chunk == b"":
                raise Exception("Null bytes recieved")

            data += data_chunk.decode()
                
            if "{END_BLOCK}" in data:
                return data

    def run(self):  
        public_key_fetch_attempts = 0
        try:
            while self.connected:
                data = self.listen_for_data_chunk()
                self.on_recv_data(data)
                print(data)

                if self.public_key == None:
                    print("Failed to get public key from client.")                
                    self.request_public_key()
                    public_key_fetch_attempts += 1
                    if public_key_fetch_attempts == 3:
                        self.cli.connected = False

                    continue
                
                elif public_key_fetch_attempts > 0:
                    public_key_fetch_attempts = 0
                
        except JSONDecodeError:
            print("Failed to decode json. consult devs")
            self.__on_client_disconnect()
        except Exception as e:
            print(e)
            self.__on_client_disconnect()
            

