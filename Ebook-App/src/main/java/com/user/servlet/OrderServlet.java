package com.user.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.DAO.BookOrderImpl;
import com.DAO.CartDAOImpl;
import com.DB.DBConnect;
import com.entity.BookDtls;
import com.entity.Book_Order;
import com.entity.Cart;
import com.mysql.cj.Session;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;//added

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			HttpSession session = req.getSession();
			int id = Integer.parseInt(req.getParameter("id"));
			String name = req.getParameter("username");
			String email = req.getParameter("email");
			String phno = req.getParameter("phno");
			String address = req.getParameter("address");
			String landmark = req.getParameter("landmark");
			String city = req.getParameter("city");
			String state = req.getParameter("state");
			String pincode = req.getParameter("pincode");
			String paymentType = req.getParameter("payment");

			String fullAdd = address + ", " + landmark + ", " + city + ", " + state + ", " + pincode;

			// System.out.println(name+" "+email+" "+phno+" "+paymentType);

			CartDAOImpl dao = new CartDAOImpl(DBConnect.getconn());
			List<Cart> blist = dao.getBookByUser(id);
			if (blist.isEmpty()) {
				session.setAttribute("failedMsg", "Add Item");
				resp.sendRedirect("checkout.jsp");
			} else {

				BookOrderImpl dao2 = new BookOrderImpl(DBConnect.getconn());
				Book_Order o = null;

				ArrayList<Book_Order> orderList = new ArrayList<Book_Order>();
				Random r = new Random();
				for (Cart c : blist) {
					// System.out.println(c.getBookName()+" "+c.getAuthor()+" "+c.getAuthor());
					o = new Book_Order();
					o.setOrderId("BOOK-ORD-00" + r.nextInt(1000));
					o.setUsername(name);
					o.setEmail(email);
					o.setPhno(phno);
					o.setFulladd(fullAdd);
					o.setBookName(c.getBookName());
					o.setAuthor(c.getAuthor());
					o.setPrice(c.getPrice() + "");
					o.setPaymentType(paymentType);

					orderList.add(o);
				}

				if ("noselect".equals(paymentType)) {
					session.setAttribute("failedMsg", "Choose Payment Method to proceed");
					resp.sendRedirect("checkout.jsp");
				} else {
					boolean f = dao2.saveOrder(orderList);

					if (f) {
						resp.sendRedirect("order_success.jsp");
					}

					else {
						session.setAttribute("failedMsg", "Your Order failed");
						resp.sendRedirect("checkout.jsp");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
