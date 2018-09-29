# ConsistentHashTest

在阅读一篇关于一致性哈希算法的[博客](https://medium.com/@dgryski/consistent-hashing-algorithmic-tradeoffs-ef6b8e2fcae8)后，将一些提及到的算法用Java实现并做了个对比。

以下是将10000个key哈希到5个节点上以及删除两个节点后的分布。

## Ring Hash
![](https://raw.github.com/limicala/DogAvaj/master/ConsistentHashTest/images/ConsistentHash.png)

## Ring Hash With Virtual Nodes
![](https://raw.github.com/limicala/DogAvaj/master/ConsistentHashTest/images/ConsistentHashVN100.png)

## 有限分布算法
![](https://raw.github.com/limicala/DogAvaj/master/ConsistentHashTest/images/LimitDistributeHash.png)

## Maglev Hash
![](https://raw.github.com/limicala/DogAvaj/master/ConsistentHashTest/images/MaglevHash.png)

## Multi-Probe Hash
![](https://raw.github.com/limicala/DogAvaj/master/ConsistentHashTest/images/MultiProbeHash.png)

## 参考链接

- [Consistent Hashing: Algorithmic Tradeoffs](https://medium.com/@dgryski/consistent-hashing-algorithmic-tradeoffs-ef6b8e2fcae8)
- [《有限分布算法》来了！ 抛弃一致性哈希吧](http://sulin.iteye.com/blog/1915431)
- [clohfink/RendezvousHash](https://github.com/clohfink/RendezvousHash)
- [[論文中文導讀] Maglev : A Fast and Reliable Software Network Load Balancer (using Consistent Hashing)](http://www.evanlin.com/maglev/)
- [New Hashing Algorithms for Data Storage](http://www.snia.org/sites/default/files/SDC15_presentations/dist_sys/Jason_Resch_New_Consistent_Hashings_Rev.pdf)
- [【翻译/介绍】jump Consistent hash:零内存消耗，均匀，快速，简洁，来自Google的一致性哈希算法](https://blog.helong.info/blog/2015/03/13/jump_consistent_hash/)