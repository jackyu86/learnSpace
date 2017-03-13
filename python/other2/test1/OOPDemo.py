

class rec:pass



rec.name='asdadas'

def upperName(self):
    print(self.name.upper())
    #self.name=self.name.upper()
    self.name.upper()




upperName(rec)

print(rec.name)




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

#继承
class Manager(Person):
    def giveRaise(self,percent,bonus=.10):
        Person.giveRaise(self,percent+bonus)


if __name__ == '__main__':
    jack = Person(name='jack yu',age=11,pay=130)
    print(jack)
    print(jack.getFirstName())
    print(jack.getLastName())
    jack_m = Manager(name='M_jack yu',age=30,pay=5000)
    print(jack_m)
    jack_m.giveRaise(.10)
    print(jack_m.pay)
    # 特殊类属性
    print(jack.__class__.__name__)#Person
    print(Manager.__class__)#type
    print(jack_m.__class__)#<class '__main__.Manager'>
    print(type(jack_m.__dict__.keys()))
    if jack_m.__class__ is Manager:
        print('--->')
    else:
        print('<---')
    #1
    print('------------------------')
    for key in jack_m.__dict__:
        print(key,'---',getattr(jack_m,key))
    #2
    print('------------------------')
    for (a,b) in jack_m.__dict__.items():
        print(a,'--',b)
    #3
    print('------------------------')
    print([ (str(a)+','+str(b))  for a,b in jack_m.__dict__.items()])


