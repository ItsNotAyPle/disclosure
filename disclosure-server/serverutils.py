import json


def extract_json_data_from_block(full_string:str):
    full_string = full_string.replace("{START_BLOCK}", "")
    full_string = full_string.replace("{END_BLOCK}", "")
    data = json.dumps(full_string)
    
    
    print(f"Recieved: {data}") # debug only
    return data

# def prepare_json_data_block(data):
    