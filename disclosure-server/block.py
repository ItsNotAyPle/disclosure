from dataclasses import dataclass
import json

# TODO: change these to int types later on?
@dataclass
class BlockType:
    SVR_REQ_PUB_KEY = "SVR_REQ_PUB_KEY"
    CLI_RES_PUB_KEY = "CLI_RES_PUB_KEY"
    SVR_RES_RECV_PUB_KEY = "SVR_RES_RECV_PUB_KEY"
    SVR_RES_NEW_CONNECTION = "SVR_RES_NEW_CONNECTION"
    MESSAGE = "MESSAGE"


class BlockPacket:
    def __init__(self, full_string):
        full_string = full_string.replace("{START_BLOCK}", "")
        full_string = full_string.replace("{END_BLOCK}", "")
        print(full_string)
        
        self.json_data = json.loads(full_string)
        self.block_type = self.json_data['packet_type']

    @staticmethod
    def create_block_packet(block_type:BlockType, data:dict) -> str:
        full_string = "{START_BLOCK}"
        full_string += json.dumps({"packet_type":block_type, "data":data})
        full_string += "{END_BLOCK}"

        return full_string
