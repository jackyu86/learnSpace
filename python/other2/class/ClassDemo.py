class Worker:
    name=''
    pay=0
    def __init__(self,name,pay):
        self.name=name
        self.pay=pay

    def getName(self):
        return self.name[-1:2]

    def getPay(self,ranges):
        self.pay*=(1.0+ranges)



