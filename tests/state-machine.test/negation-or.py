x = int()

# not (x < 0 or x > 10) == 0 <= x <= 10

if not (x < 0 or x > 10):
    if x < 5:
        y = x     # [0..4]
    else:
        y = x     # [5..10]
else:
    y = x         # [..-1] [11..]

print y
