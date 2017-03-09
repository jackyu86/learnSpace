
class C2:
    x=1

class C1:
    y=2

class C3(C2,C1):
    def __init__(self,name):
        self.name=name


    