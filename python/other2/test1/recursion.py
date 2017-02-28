

aa = 0;

def mysum(l):
    global aa
    if not l:
        return 0
    else:
       # print(l[0],"+",[l[1:]],"------")
        print((l[0]+mysum(l[1:])))
        aa+=1;
        print("------>",aa)
        return l[0]+mysum(l[1:])

a=mysum([1,2,3,4,5])

