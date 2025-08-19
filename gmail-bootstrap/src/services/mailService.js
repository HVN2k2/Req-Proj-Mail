// Mail Service để quản lý việc fetch mail
class MailService {
  constructor() {
    this.baseURL = 'http://localhost:8080/mail-project/api/mail'
    this.isFetching = false
    this.lastFetchTime = null
  }

  // Fetch mail từ IMAP server cho user đang đăng nhập
  async fetchMails() {
    if (this.isFetching) {
      console.log('⏰ Already fetching mails, skipping...')
      return { success: false, message: 'Already fetching' }
    }

    try {
      this.isFetching = true
      console.log('🔄 Fetching mails for logged-in user...')

      const token = localStorage.getItem('token')
      const userEmail = localStorage.getItem('userEmail')
      
      const headers = {
        'Content-Type': 'application/json'
      }

      if (token) {
        headers['Authorization'] = `Bearer ${token}`
      }

      // Sử dụng endpoint fetch-now (sẽ tự động fetch cho user đang đăng nhập)
      const response = await fetch(`${this.baseURL}/fetch-now`, {
        method: 'POST',
        headers
      })

      if (response.ok) {
        const result = await response.json()
        this.lastFetchTime = new Date()
        console.log('✅ Mail fetch completed for user:', userEmail, result)
        return result
      } else {
        console.error('❌ Failed to fetch mails:', response.status)
        return { success: false, message: `HTTP ${response.status}` }
      }
    } catch (error) {
      console.error('❌ Error fetching mails:', error)
      return { success: false, message: error.message }
    } finally {
      this.isFetching = false
    }
  }

  // Fetch mail cho user cụ thể (cho admin)
  async fetchMailsForUser(userEmail) {
    if (this.isFetching) {
      console.log('⏰ Already fetching mails, skipping...')
      return { success: false, message: 'Already fetching' }
    }

    try {
      this.isFetching = true
      console.log('🔄 Fetching mails for specific user:', userEmail)

      const token = localStorage.getItem('token')
      const headers = {
        'Content-Type': 'application/json'
      }

      if (token) {
        headers['Authorization'] = `Bearer ${token}`
      }

      const response = await fetch(`${this.baseURL}/fetch-user/${encodeURIComponent(userEmail)}`, {
        method: 'POST',
        headers
      })

      if (response.ok) {
        const result = await response.json()
        this.lastFetchTime = new Date()
        console.log('✅ Mail fetch completed for user:', userEmail, result)
        return result
      } else {
        console.error('❌ Failed to fetch mails for user:', userEmail, response.status)
        return { success: false, message: `HTTP ${response.status}` }
      }
    } catch (error) {
      console.error('❌ Error fetching mails for user:', userEmail, error)
      return { success: false, message: error.message }
    } finally {
      this.isFetching = false
    }
  }

  // Kiểm tra xem có nên fetch mail không
  shouldFetch() {
    if (!this.lastFetchTime) return true
    
    const now = new Date()
    const timeSinceLastFetch = (now - this.lastFetchTime) / 1000
    
    // Chỉ fetch nếu đã qua 30 giây
    return timeSinceLastFetch > 30
  }

  // Lấy thời gian fetch cuối cùng
  getLastFetchTime() {
    return this.lastFetchTime
  }

  // Kiểm tra trạng thái fetching
  isCurrentlyFetching() {
    return this.isFetching
  }

  // Fetch mail nếu cần thiết
  async fetchIfNeeded() {
    if (this.shouldFetch()) {
      return await this.fetchMails()
    } else {
      const timeSinceLastFetch = Math.round((new Date() - this.lastFetchTime) / 1000)
      console.log(`⏰ Skipping fetch - last fetch was ${timeSinceLastFetch}s ago`)
      return { success: false, message: `Skipped - last fetch was ${timeSinceLastFetch}s ago` }
    }
  }

  // Lấy thống kê mail
  async getMailStats() {
    try {
      const token = localStorage.getItem('token')
      const headers = {
        'Content-Type': 'application/json'
      }

      if (token) {
        headers['Authorization'] = `Bearer ${token}`
      }

      const response = await fetch(`${this.baseURL}/stats`, {
        method: 'GET',
        headers
      })

      if (response.ok) {
        return await response.json()
      } else {
        console.error('❌ Failed to get mail stats:', response.status)
        return null
      }
    } catch (error) {
      console.error('❌ Error getting mail stats:', error)
      return null
    }
  }
}

// Export singleton instance
export default new MailService()
