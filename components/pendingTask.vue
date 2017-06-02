<template>
  <div class="dataBar" v-loading.body="loading">
    <Sreach :data = "sreachObj" v-on:childMessage = "getPendingTaskDataBySreach"></Sreach> <!--载入公用搜索组件，搜索后出发API请求-->
    
    <table width="100%" border="0" v-if="PendingTaskData.length > 0">
      <tr>
        <td>bid</td>
        <td>bapp</td>
        <td>uri</td>
        <td>edate</td>
        <td>pathParemater</td>
        <td>action</td>
      </tr>
      <tr v-for = "item in PendingTaskData">
        <td>{{item.bid}}</td>
        <td>{{item.bapp}}</td>
        <td>{{item.uri}}/{{item.bid}}</td>
        <td>{{item.edate | formatDate}}</td>
        <td>{{item.pathParemater | json2string}}</td>
        <td>
          <el-button type="primary" @click="todo(item.bid)" class="el-icon-edit"> 处理</el-button>
        </td>
      </tr>
    </table>
    
    <el-row v-else>
      <el-col :span="24" style="width:100%;text-align:center;padding:15px 0;font-size:14px;"><div class="grid-content">暂无数据</div></el-col>
    </el-row>

    <Page v-show="PendingTaskData.length > pageObj.pageSize" :pageTotal="pageTotal" v-on:childMessage = "getPendingTaskDataByPage"></Page> <!--载入公用分页组件-->
  </div>
</template>

<script>
import Sreach from './sreach' // 引入搜索组件
import Page from './page' // 引入分页组件
// import * as utils from './../utils' // utils 工具

export default {
  name: 'pendingTask',
  /**
    子组件
  */
  components: {  // 注册公共组件
    Sreach, Page
  },
  /**
    model数据状态
  */
  data () {
    return {
      mock: true, // 是否为mock
      loading: true, // loading是否显示
      sreachObj: { // 用来接受子组价（搜索）的返回对象
        taskBeginDate: '', // 开始时间
        taskEndDate: '', // 结束时间
        taskSreachID: '', // 搜索任务ID
        taskSreachName: '' // 搜索任务名称
      },
      pageObj: { // 用来接受子组件（分页）的返回对象
        pageNo: 1, // 当前页
        pageSize: 10 // 每页多少
      },
      PendingTaskData: {}, // 请求数据容器
      pageTotal: 1, // 初始页码
      etype: 2 // 任务类型
    }
  },
  /**
    ready
  */
  mounted: function () {
    this.$nextTick(function () {
      this.getPendingTaskData(this.extendParams()) // 页面初始化未处理任务数据，初始化参数为空
    })
  },
  /**
    方法
  */
  methods: {
    // 合并请求参数
    extendParams: function () {
      return { // 初始化传递空值
        taskBeginDate: this.sreachObj.taskBeginDate,
        taskEndDate: this.sreachObj.taskEndDate,
        taskSreachID: this.sreachObj.taskSreachID,
        taskSreachName: this.sreachObj.taskSreachName,
        pageNo: this.pageObj.pageNo,
        pageSize: this.pageObj.pageSize
      }
    },
    // 获得未处理的任务数据
    getPendingTaskData: function (params) {
      console.log(params)
      let _this = this
      /* http请求 */
      let api = 'http://localhost:8888/api/rhdo/done/' + _this.pageObj.pageNo + '/' + _this.pageObj.pageSize + '/' + _this.etype + '/?edates=' + _this.sreachObj.taskBeginDate + '&edaten=' + _this.sreachObj.taskEndDate + '&taskID=' + _this.sreachObj.taskSreachID + '&taskName=' + _this.sreachObj.taskSreachName
      if (!_this.mock) {
        api = 'http://localhost:2225/rhdo/done/' + _this.pageObj.pageNo + '/' + _this.pageObj.pageSize + '/' + _this.etype + '/?edates=' + _this.sreachObj.taskBeginDate + '&edaten=' + _this.sreachObj.taskEndDate + '&taskID=' + _this.sreachObj.taskSreachID + '&taskName=' + _this.sreachObj.taskSreachName
      }
      _this.axios.get(api).then(
        (res) => {
          // _this.PendingTaskData = utils.formatRes(res.data.list) // 去null
          _this.PendingTaskData = res.data.todoall.list // 去null
          _this.pageObj.pageNo = res.data.pageNo
          _this.pageObj.pageSize = res.data.pageSize
          _this.pageTotal = res.data.pageTotal
          _this.loading = false // 关闭loading
          console.log(res.data.todoall)
        })
    },
    // 搜索子组件触发请求
    getPendingTaskDataBySreach: function (obj) {
      this.sreachObj = obj
      this.pageObj = { // 无需考虑页码,直接初始化
        pageNo: 1, // 当前页
        pageSize: 10 // 每页多少
      }
      this.getPendingTaskData(this.extendParams())
    },
    // 分页子组件触发请求
    getPendingTaskDataByPage: function (obj) {
      this.pageObj = obj
      this.getPendingTaskData(this.extendParams())
    },
    // todo方法
    todo: function (bid) {
      let _this = this
      _this.$msgbox({
        title: '任务处理',
        message: '确定要处理该任务吗？',
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            instance.confirmButtonLoading = true; // 点击确认按钮后UI
            instance.confirmButtonText = '任务执行中...'; // 点击确认按钮后文字
            /* 请求处理 */
            let url = 'http://localhost:8888/api/rhdo/' + bid
            if (!_this.mock) {
              url = 'http://localhost:2225/rhdo/' + bid
            }
            _this.axios.get(url).then(
              (res) => {
                console.log(res)
                instance.confirmButtonLoadcing = false;
                /* 判断后台是否处理 */
                if (res.data === 'success' || res.data === 'ok') { // 成功
                  /* 成功提示 */
                  this.$message({
                    type: 'success',
                    message: 'bid为“' + bid + '”的任务处理成功!'
                  });
                  /* 重新请求渲染list */
                  _this.pageObj = { // 无需考虑页码,直接初始化
                    pageNo: 1, // 当前页
                    pageSize: 10 // 每页多少
                  }
                  _this.getPendingTaskData(_this.extendParams())
                } else { // 失败
                  this.$message.error('bid为“' + bid + '”的任务处理失败！请重试');
                }
                done();
              }
            ).catch(function (err) {
              console.log(err); // http请求发送失败
            })
          } else {
            done();
          }
        }
      })
    }
  }
}
</script>

<style>
@import './../assets/css/table.css';
</style>
