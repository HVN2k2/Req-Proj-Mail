# Báo cáo về việc tìm hiểu các kiến trúc hệ thống hiện có

# I. Kiến trúc nguyên khối (Monolithic Architecture).

1.1. Kiến trúc đơn khối là gì ?

    Kiến trúc nguyên khối là mô hình thống nhất truyền thống để thiết kế một chương trình phần mềm . Nguyên khối , trong ngữ cảnh này, có nghĩa là "được tạo thành một khối". Theo từ điển Cambridge, tính từ nguyên khối cũng có nghĩa là "quá lớn" và "không thể thay đổi".
    Trong phát triển phần mềm, mô hình kiến ​​trúc đơn khối là mô hình mà một cơ sở mã duy nhất được sử dụng để thực hiện nhiều chức năng trong một ứng dụng . Ngoài ra, mỗi thành phần và các thành phần liên quan trong ứng dụng đều phải có mặt để mã được thực thi hoặc biên dịch và để phần mềm chạy.
    Hơn nữa, các chức năng khác nhau được liên kết chặt chẽ chứ không phải liên kết lỏng lẻo , như trong các chương trình phần mềm mô-đun , và một số chức năng như vậy là một phần của một ứng dụng duy nhất. Vì tất cả những lý do này, phần mềm độc khối thường rất phức tạp.

1.2. Các tính năng và thành phần chính của ứng dụng đơn khối.

    Hầu hết các ứng dụng đơn khối đều có những đặc điểm sau:
    - Đơn tầng. Điều này có nghĩa là nhiều thành phần được kết hợp thành một ứng dụng lớn. Do đó, các ứng dụng có xu hướng có cơ sở mã lớn , có thể khó cập nhật và quản lý theo thời gian.
    - Tự chứa và thống nhất. Chúng độc lập với các ứng dụng khác.
    - Một codebase duy nhất: Nếu một thành phần trong chương trình cần được cập nhật, các thành phần khác cũng có thể cần được viết lại, và toàn bộ ứng dụng phải được biên dịch, kiểm thử và triển khai lại.
    - Một cơ sở dữ liệu duy nhất: Chúng có thể sử dụng hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) làm nguồn dữ liệu duy nhất.

    Các ứng dụng monolithic có thể bao gồm một số hoặc tất cả các thành phần sau:
    - Phân quyền (Authorization): Để xác thực người dùng và cho phép họ sử dụng ứng dụng.
    - Giao diện trình bày (Presentation): Xử lý các yêu cầu HTTP và phản hồi bằng HTML, XML hoặc JSON.
    - Logic nghiệp vụ (Business logic): Các quy tắc nghiệp vụ nền tảng xác định chức năng và tính năng của ứng dụng.
    - Tầng cơ sở dữ liệu (Database layer): Bao gồm các đối tượng truy cập dữ liệu, thực hiện truy vấn cơ sở dữ liệu nhằm đạt được một mục tiêu cụ thể.
    - Mô-đun thông báo (Notification module): Cho phép ứng dụng gửi thông báo tự động đến người dùng, chẳng hạn qua email.

1.3. Ví dụ về kiến trúc monolithic là gì?
    Để hiểu rõ kiến trúc monolithic, hãy xem ví dụ về một ứng dụng ngân hàng. Cách hoạt động như sau:
    - Trang web của ứng dụng cho phép xác thực khách hàng.
    - Sau đó đăng nhập họ vào tài khoản.
    - Khi đã đăng nhập, người dùng có thể xem số dư tài khoản, tải về sao kê, chuyển tiền sang tài khoản khác và thực hiện các thao tác khác.

    Nhiều thành phần được tham gia vào quy trình này, bao gồm giao diện người dùng, dịch vụ backend xử lý xác thực và các thao tác của khách hàng.
    Nếu ứng dụng là dạng monolithic, nó được xây dựng và triển khai dưới dạng một khối thống nhất, tự chứa, bất kể người dùng truy cập từ đâu — máy tính để bàn, thiết bị di động, v.v. Ngoài ra, các thành phần đều gắn kết chặt chẽ với nhau, điều này có nghĩa là nếu cần thay đổi bất kỳ thành phần nào, thì mã của các thành phần liên quan khác cũng cần được chỉnh sửa theo.
    Nhiều ứng dụng nổi tiếng đã khởi đầu với kiến trúc monolithic rồi dần chuyển sang mô hình microservices. Ví dụ bao gồm:
    - Spotify
    - Netflix
    - Facebook
    - Instagram
    - Uber
    Tuy nhiên, một số ứng dụng đã quay trở lại với kiến trúc monolithic sau khi từng chuyển sang microservices. Một ví dụ điển hình là Amazon Prime.
    Một số ứng dụng vẫn trung thành với kiến trúc monolithic từ khi ra đời. Ví dụ:
    - Hệ thống quản lý nội dung WordPress
    - Nhân hệ điều hành Linux

1.4. Lợi ích của việc sử dụng kiến trúc Monolithic.
    Nhiều ứng dụng vẫn được tạo ra bằng mô hình phát triển nguyên khối vì nó mang lại một số lợi ích.
    Ví dụ, một số chương trình nguyên khối có thể có thời gian thông lượng và phản hồi tốt hơn so với các ứng dụng mô-đun, dựa trên microservice, đặc biệt nếu số lượng luồng và người dùng nhỏ. Chúng cũng có thể dễ dàng hơn để kiểm tra và gỡ lỗi vì chúng bao gồm ít yếu tố hơn và do đó liên quan đến ít biến và kịch bản thử nghiệm hơn.
    Kiến trúc nguyên khối đơn giản hóa sự phát triển trong giai đoạn đầu của vòng đời phát triển phần mềm (SDLC). Triển khai có thể được đơn giản hóa hơn nữa bằng cách sao chép ứng dụng đóng gói vào máy chủ. Một cơ sở mã duy nhất đơn giản hóa việc ghi nhật ký, quản lý cấu hình và giám sát hiệu suất ứng dụng. Cuối cùng, nhiều bản sao của ứng dụng có thể được đặt phía sau bộ cân bằng tải để cho phép tỷ lệ ngang.

1.5. Hạn chế của kiến trúc Monolithic
    Mặc dù có lợi, kiến ​​trúc nguyên khối cũng đưa ra một số nhược điểm:
    Việc sử dụng một cơ sở mã duy nhất có nghĩa là cập nhật mã cho một thành phần của ứng dụng đòi hỏi phải cập nhật cho tất cả các thành phần liên quan. Quá trình này có thể tốn thời gian, có thể trì hoãn việc phát triển và triển khai ứng dụng, cũng như hạn chế sự nhanh nhẹn và tốc độ của các nhóm phát triển. Hạn chế này trở nên đặc biệt quan trọng đối với các sản phẩm phức tạp hoặc các nhóm lớn.
    Một số ứng dụng có một cơ sở mã lớn, có thể khó hiểu. Sự hiểu biết kém cản trở các nhà phát triển thực hiện sửa đổi mã cần thiết để đáp ứng các yêu cầu kinh doanh hoặc kỹ thuật thay đổi. Khi các yêu cầu phát triển hoặc trở nên phức tạp hơn, việc thực hiện chính xác các thay đổi hơn nữa mà không cản trở chất lượng mã và hiệu suất ứng dụng.
    Theo sau mỗi bản cập nhật, các nhà phát triển phải biên dịch toàn bộ CodeBase và triển khai lại ứng dụng đầy đủ thay vì chỉ là phần được cập nhật. Điều này làm cho việc triển khai liên tục hoặc thường xuyên trở nên khó khăn, ảnh hưởng đến sự nhanh nhẹn của ứng dụng và nhóm.
    Kích thước của ứng dụng có thể tăng thời gian khởi động và phản hồi.
    Các phần khác nhau của ứng dụng có thể có các yêu cầu tài nguyên mâu thuẫn. Điều này làm cho việc tìm các tài nguyên cần thiết hơn để mở rộng quy mô ứng dụng.
    Độ tin cậy là một mối quan tâm với phần mềm nguyên khối. Một lỗi trong bất kỳ một thành phần nào có khả năng làm giảm toàn bộ ứng dụng. Xem xét ví dụ về ứng dụng ngân hàng trước: rò rỉ bộ nhớ trong mô -đun ủy quyền người dùng có thể làm cho toàn bộ ứng dụng không có sẵn cho người dùng.
    Cuối cùng, các ứng dụng nguyên khối không dễ dàng thích ứng với các công nghệ mới do quy mô và độ phức tạp của chúng, cũng như nỗ lực và chi phí liên quan đến việc tái phát triển tiềm năng. Đây có thể là một trở ngại nghiêm trọng cho các tổ chức nhỏ hoặc ngân sách, khiến họ không thể tận dụng ngôn ngữ hoặc khuôn khổ lập trình mới.

# II. Kiến trúc Microservice.

2.1. Microservice là gì ?
    Microservices là các module trong hệ thống được chia thành nhiều services nhỏ. Mỗi service sẽ thực hiện các chức năng chuyên biệt, như quản lý đơn hàng hoặc quản lý khách hàng,… và được đặt tại một server riêng, cho phép nâng cấp chỉnh sửa một cách độc lập. Các server này có thể giao tiếp thông qua các phương thức như gRPC, Rest API, lambda và không bị ảnh hưởng bởi nhau.
    Việc áp dụng kiến trúc microservices cho phép chia nhỏ chức năng của ứng dụng thành các dịch vụ nhỏ, tối ưu hóa trải nghiệm và tốc độ cho từng người dùng. Thiết kế giao diện dựa trên từng đối tượng giúp cải thiện tương thích và tốc độ, đồng thời giảm thiểu các chức năng không cần thiết.
    Bằng cách chia nhỏ chức năng thành các dịch vụ độc lập, nhà phát triển có thể tận dụng nhiều công nghệ và nền tảng đa dạng, từ đó xây dựng ứng dụng nhanh chóng hơn và đáp ứng các yêu cầu kinh doanh một cách linh hoạt hơn.

2.2. Ưu điểm của Microservice.
    Bằng cách chia nhỏ hệ thống thành các dịch vụ nhỏ, Microservices giúp giảm độ phức tạp và dễ dàng quản lý. Trong cấu trúc microservices, các dịch vụ giao tiếp với nhau thông qua Remote Procedure Call (RPC) hoặc Message – driven API, tạo ra sự kết nối linh hoạt. Đặc biệt, việc chia nhỏ mỗi dịch vụ sẽ giúp quá trình phát triển trở nên nhanh hơn, tiết kiệm thời gian hơn, khả năng nắm bắt và bảo trì hệ thống hiệu quả hơn.
    Một lợi thế quan trọng khác của kiến trúc Microservices là khả năng phát triển độc lập từng dịch vụ bởi các nhóm riêng biệt. Điều này giúp các nhà phát triển tự do lựa chọn công nghệ phù hợp cho quá trình phát triển.
    Bên cạnh đó, việc đóng gói mỗi dịch vụ vào một docker container độc lập của Microservices giúp giảm thiểu thời gian triển khai và tăng tính linh hoạt của hệ thống. Đặc biệt, khi áp dụng continuous deployment vào microservices, các nhà phát triển có thể tự động hóa quy trình triển khai liên tục mà không gây gián đoạn hoặc tác động đến các dịch vụ khác.
    Ngoài ra, cấu trúc Microservices có khả năng scale từng dịch vụ một cách độc lập. Việc scale có thể thực hiện dễ dàng bằng cách tăng số lượng phiên bản của mỗi dịch vụ, sau đó sử dụng load balancer để phân phối tải. Điều này giúp tăng khả năng xử lý và mở rộng của từng dịch vụ mà không ảnh hưởng đến toàn bộ quy trình. Khi triển khai một dịch vụ lên các máy chủ có tài nguyên phù hợp, doanh nghiệp có thể tối ưu hóa chi phí vận hành và tài nguyên sử dụng. Qua đó, việc scale linh hoạt và tối ưu giúp đảm bảo hiệu suất cao và sự ổn định của hệ thống microservices.
    Mặc dù microservices ra đời để cải thiện các vấn đề của monolith thế nhưng cấu trúc này vẫn tồn tại nhiều nhược điểm bên cạnh các ưu điểm vượt trội của nó.

2.3. Nhược điểm của Microservice.
    Trong kiến trúc Microservices, các nhà phát triển phải đối mặt với việc lựa chọn cách giao tiếp giữa các dịch vụ, có thể thông qua messaging hoặc RPC. Đồng thời, họ cũng phải xử lý các vấn đề khó khăn khi gặp phải kết nối chậm hoặc lỗi trong quá trình trao đổi các thông điệp (message). Vì vậy, microservices đòi hỏi sự phức tạp hơn với việc phát triển ứng dụng nguyên khối monolith.
    Nhược điểm tiếp theo của Microservices đến từ việc đảm bảo giao dịch phân tán. Việc cập nhật dữ liệu chính xác (all or none) vào nhiều dịch vụ nhỏ khác nhau là một nhiệm vụ khó hơn nhiều so với việc đảm bảo giao dịch được cập nhật trên nhiều bảng trong một cơ sở dữ liệu trung tâm. Theo nguyên tắc CAP, giao dịch phân tán không thể đáp ứng đồng thời cả ba điều kiện sau:
    - Consistency: Dữ liệu ở các điểm khác nhau trong mạng phải như nhau.
    - Availability: Yêu cầu gửi đi phải nhận được phản hồi.
    - Partition tolerance: Hệ thống vẫn hoạt động được ngay cả khi mạng gặp lỗi.
    Hiện tại, các công nghệ cơ sở dữ liệu phi quan hệ (NoSQL) hay message broker tốt nhất cũng chưa đạt được nguyên tắc CAP. Ngoài ra, doanh nghiệp sẽ gặp nhiều phức tạp hơn nếu triển khai thủ công microservices architecture như đã làm với monolith.

2.4. Sử dụng kiến trúc Microservice khi nào?
    Sở hữu lợi thế có thể hoạt động độc lập, microservices luôn được các nhà phát triển áp dụng khi phát triển các hệ thống lớn, phức tạp và bảo trì thường xuyên.
    Phát triển ứng dụng App Native
        Các nhà phát triển có thể tập trung vào một số Microservices cụ thể mà không cần lo về những dịch vụ khác. Điều này giúp tăng tốc độ phát triển và nhanh chóng đưa sản phẩm ra thị trường.
    Xây dựng và thiết kế Web API
        Microservices hỗ trợ cải thiện hiệu suất cho các nhóm xử lý riêng biệt thay vì sử dụng một ứng dụng chung. Lợi ích này không chỉ nâng cao hiệu suất mà còn tăng cường mức độ bảo mật cho toàn bộ phần mềm.
    Phát triển, mở rộng và tích hợp với module IoT
        Microservices cho phép Developer vượt qua rào cản ngôn ngữ và công nghệ. Họ có thể sử dụng nhiều ngôn ngữ lập trình và công nghệ khác nhau trong cùng một sản phẩm, đồng thời tích hợp với các module IoT một cách linh hoạt.

2.5. Chi phí phát triển.
    Microservice cần rất nhiều chi phí vận hành.
    Thông thường một ứng dụng trải qua rất nhiều các phase trước khi được publish hoàn toàn. Mỗi service phải trải qua build, test, deploy and run. Mỗi dịch vụ có thể được viết bằng các ngôn ngữ và chạy trên các môi trường/ hệ điều hành khác nhau, các kịch bản triển khai CI/CD, và các dependency cũng có thể khác nhau.
    Mỗi ứng dụng lại cần được triển khai theo cụm (clustering) để đáp ứng khả năng chịu lỗi và phục hồi, dẫn đến x2, x3 số lượng runtime instance so với số lượng dịch vụ ban đầu. Nếu hệ thống có 15 service, số lượng runtime instance có thể lên tới 30-50. Cộng thêm với các thành phần Load balancer và Message broker, hệ thống đã trở nên khá lớn nếu so với ứng dụng monolithic cùng chức năng.
    Do hệ thống bị phân tán , việc monitoring và tracing cũng cần được thêm vào để đảm bảo hệ thống hoạt động ổn đinh. Tránh tình trạng deadlock, hết dung lượng lưu trữ, compute bị quá tải…hay làm sao để biết được dịch vụ nào đang bị quá tải hoặc bị ngừng hoạt động để đưa ra quyết định phù hợp, hoặc làm sao để tracing được data/bug khi thực hiện một chức năng cross qua nhiều service và kênh messaging. Để ghi lại và truy vấn được log trong 30-50 instance cần một lượng tài nguyên không hề nhỏ.
    Hiện tại đã có nhiều giải pháp đáp ứng các quá trình phát triển và triển khai các microservices, nhưng thường những ứng dụng này chỉ đáp ứng một phần các công việc cần làm . Gần như nhà phát triển sẽ cần triển khai hệ thống ở một mức độ nhất định trước khi có thể bắt tay vào code business.

    Yêu cầu kĩ năng DevOps
    Một hệ thống microservices thường được triển khai qua một Container Engine/ Container Orchestration như Docker, Docker Swarm, K8S, Openshift… Những công nghệ này yêu cầu kiến thức về docker, container, hệ điều hành và commandline, chưa kể đến kiến thức về mạng, storage, environment thậm chí các kiến thức nâng cao hơn về ảo hóa, clustering của mỗi công nghệ đặc thù. Hệ thống được tạo nên từ nhiều dịch vụ nhỏ, nên quá trình CI/CD là không thể thiếu để quá trình triển khai và tích hợp được tự động. Những kiến thức trên khá xa lạ với các developer chuyên về business như Frontend hay Backend, nên hệ thống microservices yêu cầu kĩ năng về DevOps đáng kể.

    Interface Giao tiếp giữa các dịch vụ không rõ ràng
    Vì hệ thống được chia nhỏ ra thành cách thành phần riêng biệt có thể cả về hướng kĩ thuật hoặc hướng theo domain business, các dịch vụ này cũng có thể được thực hiện bởi những cá nhân hay team khác nhau, dẫn đến việc phối hợp cùng nhau để định nghĩa interface giao tiếp giữa các service-service hoặc service-message broker trở nên phức tạp và không ổn định nếu giữa các team/ cá nhân không tuân thủ theo các rule về mặt giao tiếp. Function sẽ bị break nếu một bên thay đổi kiểu dữ liệu hoặc một dữ liệu mandatory không được đáp ứng giữa các service. Một số chuẩn giao tiếp hoặc công nghệ hỗ trợ cho chuẩn hóa format API và dữ liệu như OpenAPI hay Schema registry đã giúp ích rất nhiều cho việc kết hợp giữa các dịch vụ, nhưng về bản chất nhà phát triển cần có các biện pháp để giải quyết vấn đề này.

    Multiply Effort
    Hệ thống microservices có xu hướng triển khai hầu hết các dịch vụ chính trên cùng một công nghệ hoặc ngôn ngữ để giảm effort phát triển, nhưng ngay cả khi đã triển khai trên cùng một công nghệ thì các dịch vụ cũng thường được lưu trữ code hoàn toàn tách biệt về phạm vi project code và dependency.
    Nhà phát triển thường xuyên phải đối mặt với vấn đề duplicate các đoạn code hoặc chức năng trên các service khác nhau, thậm chí ngay cả khi bắt đầu một dịch vụ mới, các cấu hình hệ thống, environment cũng thường được sử dụng lại trên các dịch vụ, các function util, secret, apikey… Điều gì sẽ xảy ra khi một đoạn code hay chức năng global cần được update tính năng hoặc fix bug, nhà phát triển cần thực hiện thay đổi đó ở tất cả các dịch vụ đang sử dụng. Vì đặc tính loose coupling của microservices nên việc duplicate các model hay code giữa các service là điều không thể tránh khỏi, nhưng cũng cần được tối ưu để giảm chi phí và rủi ro cho việc maintain hệ thống.

    Xử lý hệ thống phân tán phức tạp
    Microservices triển khai trên một hệ thống phân tán, các service thậm chí còn không cùng nằm trên một server, đây cũng chính là nguyên do dẫn đến rất nhiều vấn đề của microservices mà nếu ở hệ thống monolithic thậm chí chúng ta còn chưa nghe tới. Các vấn đề có thể kể đến như khả năng chịu lỗi, độ trễ của mạng, xử lý bất đồng bộ, transaction cross qua nhiều service, version của ứng dụng, tracing dữ liệu, khả năng tương thích ngược, cấu hình tập trung…
    - Khả năng chịu lỗi: Microservices cần có khả năng chịu lỗi khi có một thành phần trong hệ thống không hoạt động và đảm bảo business không bị ảnh hưởng khi có một dịch vụ bị downtime hoặc xảy ra lỗi trong giao tiếp.
    - Độ trễ của network: Với càng nhiều microservices, nguy cơ độ trễ của function càng lớn, vấn đề này rất quan trọng với các ứng dụng yêu cầu tốc độ cao và ổn định như chứng khoán, tài chính, ngân hàng.
    - Xử lý bất đồng bộ: Trong hệ thống có thể có những function không thể nào nhận được đáp ứng ngay lập tức, vậy hệ thống lại cần có cơ chế để bên request có thể nhận biết được kết quả của một request đã được đáp ứng thực sự.
    - Transaction cross qua nhiều dịch vụ: Đây là vấn đề thực sự quan trọng, Data consistency luôn là vấn đề quan trọng trong bất cứ hệ thống nào. Có lỗi trên một ứng dụng monolithic sẽ dễ dàng được giải quyết hơn do các framework hầu hết đã hỗ trợ Transactional trên các function khi thực thi. Nhưng điều này không thể lặp lại trên microservices, do các ứng dụng có nhu cầu gọi đến nhau để thực thi một business function, chưa kể đến các kênh giao tiếp khác Send and forget (messaging), dẫn đến các chức năng hoặc dữ liệu không thể rollback theo cách truyền thống.
    - Version của ứng dụng: Việc kết hợp giữa các team yêu cầu sự thống nhất về mặt interface giao tiếp và chức năng. Làm thế nào để biết một business function trong release mới đang cần code của những dịch vụ nào, version nào của dịch vụ đó, và nhu cầu rollback cả hệ thống.
    - Tracing dữ liệu: Việc debug trên hệ thống microservices không đầy đủ là khá khó khăn, nếu một function cross qua nhiều service và mỗi service chạy trên một vài instance. Điều này lại yêu cầu một hệ thống loging tập trung.
    - Khả năng tương thích ngược: Các ứng dụng khi phát triển cần được quản lý các version và thay đổi interface một cách cần trọng, cân nhắc đến khả năng tương thích ngược với các hệ thống hiện tại. Thận trọng trong các thay đổi về request/response hoặc các bussiness code. Bạn có thể phát triển thêm một tính năng những sẽ break business hiện tại.
    - Centralize configuration: Khi hệ thống phân tán mà không có giải pháp cho việc centralize các configuration, nhà phát triển sẽ phải thường xuyên thay đổi các config một các thủ công và trên nhiều dịch vụ. Việc này khá mất effort và không đảm bảo tính chính xác/ ổn định của hệ thống.

# III.Kiến trúc Modular Monolithic.

3.1. Kiến trúc Modular Monolithic là gì?
    Modular Monolithic Architecture là một kiểu kiến trúc phần mềm trong đó toàn bộ ứng dụng được phát triển và triển khai như một khối đơn (monolith), nhưng được tổ chức nội bộ theo từng module chức năng tách biệt. Mỗi module đại diện cho một domain độc lập như người dùng, sản phẩm, thanh toán,... và có thể có controller, service, repository riêng.

3.2. Các tính năng và thành phần chính.
    Các tính năng nổi bật:
    - Tố chức theo module : Mỗi chức năng được tách thành một module riêng biệt
    - Phân tách logic rõ ràng : Mỗi module có thể có controller, service, repository và domain model riêng.
    - Dễ bảo trì và mở rộng : Thêm tính năng hoặc chỉnh sửa module riêng ít ảnh hưởng các tính năng còn lại.
    - Triển khai đơn khối : Toàn bộ module vẫn được biên dịch và triển khai thành một ứng dụng duy nhất.
    Các thành phần chính:
    Một hệ thống modular monolithic thường có:
    - Module chức năng (Domain Modules) : user, product, order, payment,…
    - Module chung (Share/Common) : Chứa các class dùng chung: DTO, utils, config, exception...
    - Entry point : Lớp khởi động hệ thống, thường là Application.java trong Spring Boot.

3.3. Ưu điểm và hạn chế của kiến trúc Modular.
    Kiến trúc Modular Monolithic mang lại nhiều lợi ích đáng kể trong quá trình phát triển phần mềm. Việc tổ chức hệ thống thành các module riêng biệt giúp mã nguồn trở nên rõ ràng, dễ đọc và dễ bảo trì, đặc biệt khi làm việc trong các nhóm phát triển lớn. Kiến trúc này cũng hạn chế được các phụ thuộc vòng giữa các thành phần, đồng thời giữ nguyên sự đơn giản trong triển khai và vận hành như một hệ thống monolithic thông thường. Ngoài ra, Modular Monolithic còn phù hợp với các quy trình DevOps hiện đại và tạo nền tảng thuận lợi để chuyển đổi dần sang microservices trong tương lai khi cần thiết. Tuy nhiên, kiến trúc này cũng tồn tại một số hạn chế, như không thể mở rộng độc lập từng module khi cần, dẫn đến việc toàn bộ hệ thống phải chịu tải chung. Việc sử dụng cùng một nền tảng công nghệ cho tất cả module cũng làm giảm tính linh hoạt, và nếu một module gặp lỗi nghiêm trọng thì có thể ảnh hưởng đến toàn bộ hệ thống. Do đó, mặc dù Modular Monolithic là giải pháp cân bằng giữa tính đơn giản và khả năng tổ chức tốt, nó vẫn không phù hợp với các hệ thống có quy mô cực lớn hoặc yêu cầu phân tán cao.