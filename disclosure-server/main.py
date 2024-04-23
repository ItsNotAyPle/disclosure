from server import ServerHandler

class Main:
    def __init__(self):
        self.__server_instance = ServerHandler()
    
    def get_server_instance(self):
        return self.__server_instance

if __name__ == '__main__': 
    Main().get_server_instance().run()