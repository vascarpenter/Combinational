# SD prompts Combinational Reorderer
# This program converts clipboard.

```
# header comment
# header comment continues until # + number comment lines
# 1 .. series 1
some prompt
# 2 .. series 2
another prompt
# 3 .. series 3
just another prompt
```
# to  2, 1, 3 order
```
# header comment
# header comment continues until # + number comment lines
# 2 .. series 2
another prompt
# 1 .. series 1
some prompt
# 3 .. series 3
just another prompt
```
# reset button reorders prompts as #+num specifies order

# why you make this?
- Stable diffusion wildcard extension "Dynamic Prompts - Combinatorial generation" will extract all prompts line by line, but it's not sequential order..
- for example

- 1
- 2 1
- 2 1 3
- 2 1 3 4
- 2 1 5 3 4
- 2 1 5 6 3 4
- 2 7 1 5 6 3 4
- 2 7 1 5 6 3 4 8
- 2 9 7 1 5 6 3 4 8
- 2 9 7 1 5 6 3 10 4 8
- 2 9 7 1 5 6 3 10 4 8 11
- 2 9 12 7 1 5 6 3 10 4 8 11
- 2 9 12 7 1 5 6 3 10 13 4 8 11
- 2 9 12 7 1 5 6 3 10 13 4 8 11 14
- 2 9 12 7 1 5 6 3 15 10 13 4 8 11 14
- 2 9 12 7 1 5 6 3 15 10 13 4 16 8 11 14
- 2 9 12 7 1 5 6 3 15 10 13 4 16 17 8 11 14
- 2 9 12 7 1 5 18 6 3 15 10 13 4 16 17 8 11 14
- 19 2 9 12 7 1 5 18 6 3 15 10 13 4 16 17 8 11 14
- 19 2 9 12 7 1 5 18 6 3 20 15 10 13 4 16 17 8 11 14
- 19 2 9 12 7 21 1 5 18 6 3 20 15 10 13 4 16 17 8 11 14
