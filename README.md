# IPv4 Counter

The program counts the number of <u>unique</u> ip addresses from the input file 
according to the following requirements:

- File size is unlimited
- The program should use as little memory & time as possible

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

## Integer representation

The first optimization that comes to mind is to use a 
[32-bit integer representation](https://en.wikipedia.org/wiki/IPv4#Address_representations)
of the ip address. This will significantly reduce memory requirements, but not enough.

### Calculations

Disclaimer
> The calculations do not pretend to take into account the entire real memory capacity.
> They are rather relative and try to show the difference between the approaches.

| data type       | length | memory (bytes)    |
|-----------------|--------|-------------------|
| String (latin1) | 7..15  | 7..15 + meta data |
| int (primitive) | N/A    | 4 (const)         |
| Integer         | N/A    | 4 + meta data     |

To hold the maximum ip address range in `HashSet<String>` is

```text
(7 + 15) / 2 bytes * 2^32 - 1
 = 11 bytes * 4,294,967,295
 = 47,244,640,245 bytes 
 = 46,137,343kb
 = 45,055mb
 = 44gb + meta data
```

versus `HashSet<Integer>`

```text
4 bytes * 2^32 - 1
 = 4 bytes * 4,294,967,295
 = 17,179,869,180 bytes
 = 16,777,216kb
 = 16,384mb
 = 16gb + meta data
```

In addition, the file may be larger than the maximum ip address range.
This means that the solution have to be able to save data from memory to disk.

## Partitioning

The basic idea is to be able to save and load `HashSet` to and from disk
depending on partitions. This allows you to divide the data into parts that are 
available for storage in memory. This means that each partition has its own 
`HashSet` to save and load to or from disk.

### File format

The file format should contain the size of the buckets in the `HashSet` and then 
the data that can be read. Due to the integer data type, binary format is the best option.

```text
[size][int1][int2][int3][int4]...[intN]
```

### Calculations

The partition can be the first octet of the ip address. This means that the number 
of ip addresses belonging to the first octet value is a maximum of 3 bytes.

```text
o1 = 0..255 #first octet
partition = o1
4 bytes * 2^24 - 1 
 = 4 bytes * 16,777,215
 = 67,108,864 bytes
 = 65,536kb
 = 64mb
```

This amount of data can be stored and loaded as needed when the program processes 
ip addresses of the first octet. 

However, determining partitions by values from the first octet may not be the best solution. 
A more efficient approach is to control the number of partitions. For example, by decreasing 
or increasing the number of partitions by the degree of the number 2.

```text
o1 = 0..255 #first octet
partition = p(o1), where 1 <= p(o1) <= 256
```

Memory utilization depending on the number of partitions is described in the following table.
Note that memory usage is specified as maximum for the maximum range of ip addresses.

| p  | p size | range size    | ranges of octet 1                   | memory usage      |
|----|--------|---------------|-------------------------------------|-------------------|
| p0 | 1      | 256 / 1 = 256 | 0..255                              | 64mb * 256 = 16gb |
| p1 | 2      | 256 / 2 = 128 | 0..127, 128..255                    | 64mb * 128 = 8gb  |
| p2 | 4      | 256 / 4 = 64  | 0..63, 64..127, 128..191, 192..255  | 64mb * 64 = 4gb   |
| pN | 256    | 256 / 256 = 1 | 0, 1, 2, 3, 4, 5, 6, 7, 8, ..., 255 | 64mb * 1 = 64mb   |

To assign ip addresses to partitions, the following function can be used. 
To calculate a module in Java a bitwise modulo can be used.

```text
p(o1) = o1 mod p size
```

The main disadvantage of this solution is the need to store and load hash sets when the 
partition changes. In the worst case this may happen when processing each line.

## Partitioning & caching of input data

To make the program flexible to configure and avoid unnecessary work with the file system, 
input data from different partitions that are not currently being processed is stored in 
a cache. When the cache size threshold is reached, the program changes the partition depending 
on the winner. The winning partition is the partition that appears in the cache the most times.
And then the process continues.

## Custom stored HashSet

To reduce memory usage and boxing-unboxing primitives, a custom `HashSet` can be used that stores 
primitive integers. Additionally, it can load data directly from a file.