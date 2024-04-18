from Crypto.PublicKey import RSA

import socket
import json

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEPORT, 1)
s.bind((socket.gethostbyname(socket.gethostname()), 8000))
s.listen()

def create_block_packet(block_type:str, data:dict) -> str:
    full_string = "{START_BLOCK}"
    full_string += json.dumps({"packet_type":block_type, "data":data})
    full_string += "{END_BLOCK}"
    
    return full_string


while True:
    cli, addr = s.accept()
    print("Recieved connection!")
    full_string = ""
    
    while True:
        cli_data = cli.recv(8)
        if (cli_data == b""): break
        
        full_string += cli_data.decode()
        if "{END_BLOCK}" in full_string:
            break

    # full_string = full_string.replace("{START_BLOCK}", "")
    # full_string = full_string.replace("{END_BLOCK}", "")
    
    try:
        # data = json.loads(full_string)
        # key = data['data']['public_key']
        # print(data)
        # print(key)
        # print(msg)
        # print("sending data...")
        # cli.send(create_block_packet("TEST1", None).encode())
        # cli.send(create_block_packet("TEST2", None).encode())
        print("sent")
    except Exception as e:
        print(f"Exception: {e}")
        continue
    # rsa.dec
