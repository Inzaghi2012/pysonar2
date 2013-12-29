x = int()
y = int()

if x < 3 or y > 10:
    if x < 4 or y > 8:
        y = 42      # here
    else:
        y = 'hi'

print y
