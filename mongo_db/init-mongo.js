db.createUser(
    {
        user: "username_here",
        pwd: "secret_here",
        roles: [
            {
                role: "readWrite",
                db: "admin"
            }
        ]
    }
);