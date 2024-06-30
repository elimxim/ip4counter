# IPv4 Counter

The program counts the number of unique ip addresses from the input file
according to the following requirements.

---
1. [Requirements](#requirements)
2. [Naive approach](#naive-approach)
3. [Target solution](#target-solution)  
   3.1. [Pros & Cons](#pros--cons)  
   3.2. [Testing & measuring](#testing--measuring)
4. [Installation](#installation)
5. [Not very good solutions](#not-very-good-solutions)  
   5.1. [IPv4 Integer representation](#ipv4-integer-representation)  
   5.2. [IntHashSet - custom HashSet with Java primitives](#inthashset---custom-hashset-with-java-primitives)  
   5.3. [Pagination/Partitioning & Caching](#paginationpartitioning--caching)
---

## Requirements

- File size is unlimited
- The program should use as little memory & time as possible
- Java 17 or higher

The file contains ip addresses line by line as follows:

```text
127.0.0.1
4.64.10.13
19.45.6.140
19.45.6.140
3.42.11.8
...
```

## Naive approach

The naive solution is to read the file line by line and use `HashSet<String>` to
detect duplicates. The main drawback of this solution is huge memory requirements.

To store the maximum range of ip addresses in `HashSet<String>`, the following memory is used:

```text
((7 B + 15 B) / 2) * 2^32 - 1
 = 11 B * 4,294,967,295
 = 47,244,640,245 B
 = 46,137,343 KiB
 = 45,055 MiB
 = 44 GiB + meta data
```

## Target solution

It is known that the values of ip-addresses lie in the range from _0_ to 
_2<sup>32</sup>_, and an ip-address can be represented as a _32_-bit integer 
value. TThe basic idea is to use an array, each element of which indicates 
the presence or absence of an ip address and has two states:

- _0_ - ip address is absent and
- _1_ - ip address is present.

Then, to check if the ip address is unique, we need to check the state of the 
element of this array whose index is equal to the _32_-bit integer value 
of the ip address.

According to possible values the most efficient structure in terms of memory 
utilizing would be an _array_ of bits. Then the length _N_ of the array 
is _2<sup>32</sup>_ bits. 

The _array_ structure is shown in the following figure:

```text
ip (int):  0  1  2  3  4  5  -->  N
   array: [0][0][0][0][1][0] ... [0]
                       |
                    ip 4 is present
                 0.0.0.4 in text representation
```

Memory usage:

> Bit: 2<sup>32</sup> = 4,294,967,296  
> Byte: 2<sup>29</sup> = 536,870,912  
> KiB: 2<sup>19</sup> = 524,288  
> MiB: 2<sup>9</sup> = 512  
> GiB: 2<sup>-1</sup> = 0.5

Java doesn't provide an array of bits, but instead we can use an array 
of byte, short, int, or long. This means that each array element contains 
_S_ bits, depending on the type selected. Then the length _M_ of the _array_
could be _2<sup>32</sup> / S_ or _2<sup>32 - log2(S)</sup>_. 

| data type | S  | log2(S) | M                            | memory usage                      |
|-----------|----|---------|------------------------------|-----------------------------------|
| byte      | 8  | 3       | 2<sup>29</sup> = 536,870,912 | 2<sup>29</sup> * 8 bit = 512 MiB  |
| short     | 16 | 4       | 2<sup>28</sup> = 268,435,456 | 2<sup>28</sup> * 16 bit = 512 MiB |
| int       | 32 | 5       | 2<sup>27</sup> = 134,217,728 | 2<sup>27</sup> * 32 bit = 512 MiB |
| long      | 64 | 6       | 2<sup>26</sup> = 67,108,864  | 2<sup>26</sup> * 64 bit = 512 MiB |

The _array_ structure is shown in the following figure:

```text
   array:              [0]                           [i]                ...  [M]
                        |                             |   
            |------------------------|    |------------------------|
ip (int):   0   1   2   3   4  --> S-1    i  i+1 i+2 i+3 i+4 --> i+S-1
           [0] [0] [0] [0] [1] ... [0]   [0] [0] [0] [0] [0] ...  [0]
                            |             
                         ip 4 is present  
                      0.0.0.4 in text representation
```

To check if there is a _32_-bit ip address _p_ in the _array_ the following steps 
should be performed:

- find an element _e_ by executing `array[p / S]`, but it doesn't work in Java 
  because all integer types use the high bit as the sign; therefore, to perform 
  unsigned division, we can use the right shift operator `>>>>`, as shown:  
  ```text
  array[p >>> log2(S)]
  ```
- check a bit in the found element by executing  
  ```text
  e & (1 << (p & (S - 1)))
  ```
  where
  - `p & (S - 1)` - finding the bit position using modulus of dividing `p` by `S`; 
    the `p % S` operator does not work because of signed integer types in Java
  - `1 << (...)` - shifting the bit to the left by the desired position
  - `e & (...)` - bit check using the logical _AND_
- a result of `0` means that the ip address is unique (has not been processed before), 
  other values greater than `0` means that the ip address is a duplicate

If the result was `0`, the next action is to write the 32-bit ip address _p_
to the _array_ as follows:

```text
array[p >>> log2(S)] |= (1 << (p & (S - 1)))
```

where the `|=` operator applies the logical _OR_ and writes the result in the element _e_.

To count unique ip addresses, the program calculates the difference between 
all ip addresses and duplicates.

### Pros & Cons

Pros:
- the program uses a relatively small amount of memory even when processing very large files
- with proper micro-optimization, this seems like the fastest solution

Cons:
- the program does not react to the amount of input data and uses the same amount of memory 
  even when processing a small file
- the solution doesn't scale to work with IPv6 addresses

### Testing & measuring

The program was tested on the following file
([download link](https://ecwid-vgv-storage.s3.eu-central-1.amazonaws.com/ip_addresses.zip)):

| size   | total ip addresses | unique ip addressed | avg time |
|--------|--------------------|---------------------|----------|
| 106 GB | 8,000,000,000 (8B) | 1,000,000,000 (1B)  | ~18 min  |

Environment:
- OS: `Microsoft Windows 11 Home 10.0.22631 N/A Build 22631`
- Java version:
  ```shell
  java version "20.0.1" 2023-04-18
  Java(TM) SE Runtime Environment (build 20.0.1+9-29)
  Java HotSpot(TM) 64-Bit Server VM (build 20.0.1+9-29, mixed mode, sharing)
  ```
- CPU: `11th Gen Intel(R) Core(TM) i9-11900H @ 2.50GHz`
- RAM: `32.0 GB (31.7 GB usable)`
- SSD: `NVMe PC711 NVMe SK hynix 1TB`

## Installation

> Necessary environment:
> - Installed Java 20+

1. Clone the repository:
   ```shell
   $ git clone https://github.com/elimxim/ip4counter
   ```
2. Run the build script:
   ```shell
   $ ./gradlew build
   ```
3. To unzip the distribution, you can find it in the directory:
   ```shell
   $ cd build/distributions/ip4counter-1.X
   ```

To run the program:

```shell
$ ./ip4counter <path>
```

## Not very good solutions

### IPv4 Integer representation

The first optimization that comes to mind is to use a
[32-bit integer representation](https://en.wikipedia.org/wiki/IPv4#Address_representations)
of the ip address. This will significantly reduce memory requirements, but not enough.

| data type       | length | memory (bytes)    |
|-----------------|--------|-------------------|
| String (latin1) | 7..15  | 7..15 + meta data |
| int (primitive) | N/A    | 4 (const)         |
| Integer         | N/A    | 4 + meta data     |

To store the maximum range of ip addresses in `HashSet<Integer>`, the following memory is used:

```text
4 B * 2^32 - 1
 = 4 B * 4,294,967,295
 = 17,179,869,180 B
 = 16,777,216 KiB
 = 16,384 MiB
 = 16 GiB + meta data
```

In addition, the file may be larger than the maximum ip address range.
This means that the solution have to be able to save data from memory to disk.

### IntHashSet - custom HashSet with Java primitives

To reduce memory usage and boxing-unboxing primitives, a custom `HashSet` can be used
that stores primitive integers. Additionally, it can load data directly from a file.

The main disadvantage of this structure is the complexity of implementation.

### Pagination/Partitioning & Caching

The basic idea is to be able to save and load `HashSet<Integer>` to and from disk
depending on page/partition. This allows the data to be divided into parts that are
available for storage in memory. This means that each page/partition has its own
`HashSet<INteger>` to save and load to or from disk as quick as possible.

The file format should contain the size of the buckets in the `HashSet` and then the
data that can be read. Due to the integer data type, binary format is the best option.

```text
[size][int1][int2][int3][int4]...[intN]
```

The page/partition can be the first octet of the ip address. This means that the number
of ip addresses belonging to the first octet value is a maximum of 3 bytes.

```text
o1 = 0..255 #first octet
page/partition = o1
4 B * 2^24 - 1 
 = 4 B * 16,777,215
 = 67,108,864 B
 = 65,536 KiB
 = 64 MiB
```

This amount of data can be stored and loaded as needed when the program processes
ip addresses of the first octet.

However, determining page/partition by values from the first octet may not be the best
solution. A more efficient approach is to control the number of page/partition. For
example, by decreasing or increasing the number of page/partition by the degree of
the number 2.

```text
o1 = 0..255 #first octet
page/partition = p(o1), where 1 <= p(o1) <= 256
```

Memory utilization depending on the number of pages/partitions is described in the following
table. Note that memory usage is specified as maximum for the maximum range of ip addresses.

| p  | p-size | range-size    | octet1 ranges                       | memory usage          |
|----|--------|---------------|-------------------------------------|-----------------------|
| p0 | 1      | 256 / 1 = 256 | 0..255                              | 64 MiB * 256 = 16 GiB |
| p1 | 2      | 256 / 2 = 128 | 0..127, 128..255                    | 64 MiB * 128 = 8 GiB  |
| p2 | 4      | 256 / 4 = 64  | 0..63, 64..127, 128..191, 192..255  | 64 MiB * 64 = 4 GiB   |
| pN | 256    | 256 / 256 = 1 | 0, 1, 2, 3, 4, 5, 6, 7, 8, ..., 255 | 64 MiB * 1 = 64 MiB   |

To assign ip addresses to pages/partitions, the following function can be used.
To calculate a module in Java a bitwise modulo can be used.

```text
p(o1) = o1 mod p-size
```

To make the program flexible to configure and avoid unnecessary work with the file system,
input data from different pages/partitions that are not currently being processed is stored
in a cache. When the cache size threshold is reached, the program changes the page/partition
depending on the winner. The winning page/partition is the page/partition that appears in
the cache the most times. And then the process continues.

Memory utilization depending on the cache size is described in the following table.

| cache size     | memory usage    |
|----------------|-----------------|
| 1024           | * 4 B = 4 KiB   |
| 1,048,576      | * 4 B = 4 MiB   |
| 1,048,576 x 50 | * 4 B = 200 MiB |

To avoid additional memory allocation, the cache is never cleared.