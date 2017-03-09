class Person:
    def __init__(self,name,age=None,pay=0):
        self.name=name
        self.age=age
        self.pay=pay

    def getFirstName(self):
        return self.name.split()[0]

    def getLastName(self):
        return self.name.split()[-1]

    def giveRaise(self,percent):
        self.pay=int(self.pay*(1+percent))

    def __str__(self):
        return '[Person:%s,%s,%s]' %(self.name,self.age,self.pay)


class Manager:
    def __init__(self,name,pay):
        self.person=Person(name,30,pay)

    def giveRaise(self,percent,bonus=.10):
        self.person.giveRaise(percent+bonus)


    def __getattr__(self, item):
        return getattr(self.person,item)

    def __str__(self):
        return str(self.person)

if __name__ == '__main__':
        jack = Person(name='jack yu', age=11, pay=130)
        print(jack)
        print(jack.getFirstName())
        print(jack.getLastName())
        jack_m = Manager('jack_m yu',5000)
        print(jack_m)
        jack_m.giveRaise(.10)
        print(jack_m.pay)
        print(jack_m.__getattr__('name'))
