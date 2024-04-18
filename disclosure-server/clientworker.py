from json import JSONDecodeError
from block import BlockPacket, BlockType

import threading
import socket
import uuid

# TODO: does addr_info need to be passed?
class ClientHandler(Threading.thread):
    def __init__(self, parent_server_instance, id:uuid.UUID, cli:socket.socket, addr_info=None):
        self.server = parent_server_instance
        threading.Thread.__init__(self)
        self.id = id
        self.public_key = None
        # self.on_recv_data_cb = on_recv_data_cb
        self.addr_info = addr_info
        self.connected = True
        self.cli = cli

    def __del__(self):
        self.__on_client_disconnect()

    def __on_client_disconnect(self, data):
        connected = False
        self.cli.close()

    def send_block(self, block:BlockPacket):
        self.cli.sendall(block.prepare_block())

    def request_public_key(self):
        self.cli.sendall(BlockPacket.create_block_packet(BlockType.SVR_REQ_PUB_KEY, None))

    # '⠀⣞⢽⢪⢣⢣⢣⢫⡺⡵⣝⡮⣗⢷⢽⢽⢽⣮⡷⡽⣜⣜⢮⢺⣜⢷⢽⢝⡽⣝
    # ⠸⡸⠜⠕⠕⠁⢁⢇⢏⢽⢺⣪⡳⡝⣎⣏⢯⢞⡿⣟⣷⣳⢯⡷⣽⢽⢯⣳⣫⠇
    # ⠀⠀⢀⢀⢄⢬⢪⡪⡎⣆⡈⠚⠜⠕⠇⠗⠝⢕⢯⢫⣞⣯⣿⣻⡽⣏⢗⣗⠏⠀
    # ⠀⠪⡪⡪⣪⢪⢺⢸⢢⢓⢆⢤⢀⠀⠀⠀⠀⠈⢊⢞⡾⣿⡯⣏⢮⠷⠁⠀⠀
    # ⠀⠀⠀⠈⠊⠆⡃⠕⢕⢇⢇⢇⢇⢇⢏⢎⢎⢆⢄⠀⢑⣽⣿⢝⠲⠉⠀⠀⠀⠀
    # ⠀⠀⠀⠀⠀⡿⠂⠠⠀⡇⢇⠕⢈⣀⠀⠁⠡⠣⡣⡫⣂⣿⠯⢪⠰⠂⠀⠀⠀⠀
    # ⠀⠀⠀⠀⡦⡙⡂⢀⢤⢣⠣⡈⣾⡃⠠⠄⠀⡄⢱⣌⣶⢏⢊⠂⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⠀⢝⡲⣜⡮⡏⢎⢌⢂⠙⠢⠐⢀⢘⢵⣽⣿⡿⠁⠁⠀⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⠀⠨⣺⡺⡕⡕⡱⡑⡆⡕⡅⡕⡜⡼⢽⡻⠏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⠀⣼⣳⣫⣾⣵⣗⡵⡱⡡⢣⢑⢕⢜⢕⡝⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⣴⣿⣾⣿⣿⣿⡿⡽⡑⢌⠪⡢⡣⣣⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⡟⡾⣿⢿⢿⢵⣽⣾⣼⣘⢸⢸⣞⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    # ⠀⠀⠀⠀⠁⠇⠡⠩⡫⢿⣝⡻⡮⣒⢽⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    # No switches? 

    def on_recv_data(self, data:str):
        blockdata = BlockPacket(data)
        json_data = blockdata.json_data['data']

        if blockdata.block_type == BlockType.CLI_RES_PUB_KEY:
            self.public_key = json_data['public_key']
            print(f"Recieved public key from client [{self.public_key}]")

        elif blockdata.block_type == BlockType.MESSAGE:
            self.server.send_block_to_client(
                json_data['to'],
                blockdata
                )

    def run(self):
        data = ""
        while self.connected:
            try:
                data_chunk = cli.recv(8)
                if data_chunk == b"":
                    self.connected = False
                    continue

                data += data_chunk.decode()
                
                if "{END_BLOCK}" in data:
                    self.on_recv_data(data)
                    self.__on_client_disconnect(data)
                    data = ""
                    continue

            except JSONDecodeError:
                print("Failed to decode json. consult devs")
            except Exception as e:
                print(e.with_traceback())
                self.__on_client_disconnect(data)
            

