<template>
  <el-row class="mt20 rxr">
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageInfo.pageNo"
      :page-sizes="[10, 50, 100, 300]"
      :page-size="pageInfo.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pageTotal">
    </el-pagination>
  </el-row>
</template>

<script>
export default {
  name: 'sreach',
  data () {
    return {
      pageInfo: {
        pageNo: 1, // 当前页
        pageSize: 10 // 每页多少条
      }
    }
  },
  props: [ 'pageTotal' ], // 父组件传入的总条数
  methods: {
    handleSizeChange (val) { // 每页多少改变时
      this.pageInfo.pageSize = val
      this.submit()
    },
    handleCurrentChange (val) { // 当前页改变时
      this.pageInfo.pageNo = val
      this.submit()
    },
    submit () {
      // 传递数据至父组件，父组件进行数据搜索
      this.$emit('childMessage', this.pageInfo)
    }
  }
}
</script>

<style>
</style>
