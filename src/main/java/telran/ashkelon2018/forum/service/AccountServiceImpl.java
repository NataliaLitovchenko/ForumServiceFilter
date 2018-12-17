package telran.ashkelon2018.forum.service;

import java.time.LocalDateTime;
import java.util.Set;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import telran.ashkelon2018.forum.configuration.AccountConfiguration;
import telran.ashkelon2018.forum.configuration.AccountUserCredentials;
import telran.ashkelon2018.forum.dao.UserAccountRepository;
import telran.ashkelon2018.forum.domain.UserAccount;
import telran.ashkelon2018.forum.dto.UserProfileDto;
import telran.ashkelon2018.forum.dto.UserRegDto;
import telran.ashkelon2018.forum.exceptions.UserSecurityException;
import telran.ashkelon2018.forum.exceptions.UserConflikcException;

@Service
public class AccountServiceImpl implements AccountService {
	@Autowired
	UserAccountRepository userRepository;

	@Autowired
	AccountConfiguration accountConfiguration;

	@Override
	public UserProfileDto addUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		if (userRepository.existsById(credentials.getLogin())) {
			throw new UserConflikcException();
		}
		String hashPassword = BCrypt.hashpw(credentials.getPassword(), BCrypt.gensalt());
		//
		UserAccount userAccount = UserAccount.builder()
				.login(credentials.getLogin())
				.password(hashPassword)
				.firstName(userRegDto.getFirstName())
				.lastName(userRegDto.getLastName())
				.role("User")
				.expdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()))
				.build();
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	public UserProfileDto convertToUserProfileDto(UserAccount userAccount) {
		return UserProfileDto.builder()
				.login(userAccount.getLogin())
				.firstName(userAccount.getFirstName())
				.lastName(userAccount.getLastName())
				.roles(userAccount.getRoles())
				.build();
	}
	@Override
	public UserProfileDto editUser(UserRegDto userRegDto, String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		if (userRegDto.getFirstName() != null) {
			userAccount.setFirstName(userRegDto.getFirstName());
		}
		if (userRegDto.getLastName()!= null) {
			userAccount.setLastName(userRegDto.getLastName());
		}
		userRepository.save(userAccount);
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public UserProfileDto removeUser(String login, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount user = userRepository.findById(credentials.getLogin()).get();
		
		boolean filter1 =!(user.getRoles().contains("Moderator") || user.getRoles().contains("Admin"));
		boolean filter2 =!login.equals(credentials.getLogin());
				
		if ( filter1) {
			if (filter2) {
				throw new UserSecurityException();
			}
					
		}
		
		UserAccount userAccount = userRepository.findById(login).get();
		if (userAccount !=null) {
			userRepository.delete(userAccount);
		}
		return convertToUserProfileDto(userAccount);
	}

	@Override
	public Set<String> addRole(String login, String role, String token) {
		// FIXME
		
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount user = userRepository.findById(credentials.getLogin()).get();
		
		if (!user.getRoles().contains("Admin")) {
			throw new UserSecurityException();
		}
		UserAccount userAccount = userRepository.findById(login).orElse(null);		
		if (userAccount !=null) {
			userAccount.addRole(role);
			userRepository.save(userAccount);
		}else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public Set<String> removeRole(String login, String role, String token) {
		// FIXME
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount user = userRepository.findById(credentials.getLogin()).get();
		
		if (!user.getRoles().contains("Admin")) {
			throw new UserSecurityException();
		}
		UserAccount userAccount = userRepository.findById(login).orElse(null);
		if (userAccount !=null) {
			userAccount.removeRole(role);
			userRepository.save(userAccount);
		}else {
			return null;
		}
		return userAccount.getRoles();
	}

	@Override
	public void changePassword(String password, String token) {
		// FIXME

		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
		
		if (password.equals(credentials.getPassword())) {
			throw new UserSecurityException();
		}
		
		userAccount.setPassword(hashPassword);
		userAccount.setExpdate(LocalDateTime.now().plusDays(accountConfiguration.getExpPeriod()));
		userRepository.save(userAccount);
	}

	@Override
	public UserProfileDto login(String token) {
		AccountUserCredentials credentials = accountConfiguration.tokenDecode(token);
		UserAccount userAccount = userRepository.findById(credentials.getLogin()).get();
		return convertToUserProfileDto(userAccount);
	}

}
